package com.shoppingcart.admin.user;


import com.shoppingcart.admin.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRestController {

    @Autowired
    private UserService userService;


    @PostMapping("/user/check_email")
    public String checkDuplicateEmail(Integer id,String email){
        return userService.isEmailUnique(id, email) ? "OK" : "Duplicated";
    }
}
