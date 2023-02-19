package com.shoppingcart.admin.user;

import com.shoppingcart.admin.entity.Role;
import com.shoppingcart.admin.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {

    @Autowired
    private UserRepository repo;
    @Autowired
    private TestEntityManager entityManager;


    @Test
    public void testCreateNewUserWithOneRole(){
        Role roleAdmin = entityManager.find(Role.class,1);
        User userNamHM = new User("Khacvan@gmail.com","123456","Nguyen Khac","Van");


        userNamHM.setEnabled(true);
        userNamHM.setPhoto("hehe.jpg");
        userNamHM.addRole(roleAdmin);
        User saveUser = repo.save(userNamHM);
        assertThat(saveUser.getId()).isGreaterThan(0);

    }

    @Test
    public void testCreateNewUserWithTwoRole(){
        User userRavi= new User("Khacvand@gmail.com","123456","Nguyen Khalc1","Valn1");
        Role roleEditor = new Role(4);
        Role roleAssistant = new Role(5);
        userRavi.addRole(roleEditor);
        userRavi.addRole(roleAssistant);
        User saveUser = repo.save(userRavi);
        assertThat(saveUser.getId()).isGreaterThan(0);

    }


    @Test
    public void listUserFindAll(){
        Iterable<User> users =repo.findAll();
        users.forEach(user -> System.out.println(user.toString()));
    }
}
