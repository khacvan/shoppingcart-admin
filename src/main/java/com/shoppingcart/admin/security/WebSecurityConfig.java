package com.shoppingcart.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{


	@Bean  // @Bean đi chung với @Configuration và sẽ có 1 thg khác Autowired lại
	public UserDetailsService userDetailsService() {
		return new ShoppingUserDetailsService();
	}

	@Bean
	public PasswordEncoder passwordEncoder() { //mã hóa
		return new BCryptPasswordEncoder();
	}

	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}

	@Override  // cho trang html
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests()
				//.antMatchers("/users/**","/categories/**")
				.antMatchers("/users/**","/categories/**","/brands/**").hasAuthority("Admin") // phân quyền - muốn truy cập các đường dẫn "users/"
				//.anyRequest().permitAll()  - vào ko cần xác thực
				.anyRequest().authenticated() // tất cả các request đều phải log in
				.and()
				.formLogin()
				.loginPage("/login") // đường dẫn @GetMapping
				.usernameParameter("email") // thay username bang email => giống name bên html name ="email"
				//.passwordParameter() ko cần ghi vì trùng name bên html
				.permitAll()
				.and().logout().permitAll()
				.and()
				.rememberMe()
				.key("Abc_123") // Restart app no need login again
				.tokenValiditySeconds(7 * 24 * 60 *60);// thời gian hết hạn là 7 ngày
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		// Dùng để hiện thỉ hình, vì chưa login vào sẽ ko truy cập được các resource static,
		// dòng code này giúp mình truy cập được
		web.ignoring().antMatchers("/images/**","/js/**","/webjars/**");
	}
}