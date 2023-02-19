package com.shoppingcart.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


//annotation
//Spring BEAN, bean
//Spring IOC
@SpringBootApplication
@ComponentScan(basePackages = {"com.shoppingcart.admin.*","com.shoppingcart.admin"})  // quét qua package com.shoppingcart.admin.* để
@EnableJpaRepositories(basePackages = {"com.shoppingcart.admin.*"}) // khởi tạo Spring BEAN
@EntityScan({"com.shoppingcart.admin.*"}) // dựa vào cấu hình của các Entity để tạo table và column
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main .class, args);
    }

}