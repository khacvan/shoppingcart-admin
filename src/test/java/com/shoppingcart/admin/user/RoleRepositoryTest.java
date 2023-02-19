package com.shoppingcart.admin.user;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shoppingcart.admin.entity.Role;

import java.util.List;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class RoleRepositoryTest{
    @Autowired
    private RoleRepository repo;

    @Test
    public void testCreateFirstRole(){
        Role roleAdmin = new Role("Admin","Manager System");
        Role saveRole = repo.save(roleAdmin);
        assertThat(saveRole.getId()).isGreaterThan(0);

    }

    @Test
    public void testCreateRestRoles(){
        Role roleSalesperson = new Role("slaesperson","manage product price,"
                + " customer,shipping, orders and sales report");

        Role roleEditor = new Role("Editor", "manager categories, brands, "+"product, articles and menus");

        Role roleAssistant= new Role("Assistants", "manage Q&A");Role roleShipper = new Role("Shipper", "view product, view orders, "+" and update order status");

        repo.saveAll(List.of(roleSalesperson,roleEditor,roleShipper,roleAssistant));




    }
}
