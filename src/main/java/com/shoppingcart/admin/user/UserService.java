package com.shoppingcart.admin.user;


import com.shoppingcart.admin.entity.Role;
import com.shoppingcart.admin.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;



@Service
@Transactional
public class UserService {

    public static final int USERS_PER_PAGE =4;
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private  RoleRepository roleRepo;

    public List<User> listAll(){

        return (List<User>) userRepo.findAll(Sort.by("firstName").ascending());
    }

    public List<Role> listRoles(){
        return (List<Role>) roleRepo.findAll();
    }


    public User save (User user){
        return userRepo.save(user);
    }

    public User findById(int id) throws UserNotFoundException {
        try {
            return this.userRepo.findById(id).get();
        }
        catch(NoSuchElementException e) {
            throw new UserNotFoundException("Could not find any user with ID:"+id);
        }
    }


    public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword){
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum -1,USERS_PER_PAGE, sort);

        if(keyword != null){
            return  userRepo.findAll(keyword, pageable);
        }
        return userRepo.findAll(pageable);
    }

    public void delete(int id) throws UserNotFoundException {
        int countById = userRepo.countById(id);
        if(countById == 0) {
            throw new UserNotFoundException("Could not find any user with ID: " +id);
        }
        this.userRepo.deleteById(id);
    }

    public void setEnable(int id, boolean enable) throws UserNotFoundException {
        User user = userRepo.findById(id).get();
        if(user == null) {
            throw new UserNotFoundException("Could not find any user with ID: " +id);
        }
        user.setEnabled(enable);
        userRepo.save(user);
    }

    public void setEnableByQuery(int id, boolean enable) {
        userRepo.updateUserEnableById(id, enable);
    }


    public  boolean isEmailUnique(Integer id, String email){
        User userByEmail = userRepo.getUserByEmail(email);

        if(userByEmail == null){
            return  true;
        }
        boolean isCreatingNew = (id == null);
        if(isCreatingNew){
            if(userByEmail != null){
                return  false;
            }else {
                if(userByEmail.getId() != id){
                    return false;
                }
            }

        }
        return true;

    }

    public User getByEmail(String email) {
        return userRepo.getUserByEmail(email);
    }

    public User updateAccount(User userInForm) {
        User userInDB = userRepo.findById(userInForm.getId()).get();
        if(!userInForm.getPassword().isEmpty()){
            userInDB.setPassword(userInDB.getPassword());
//            encodePassword(userInDB);

        }

        if(userInForm.getPhoto()!=null){
            userInDB.setPhoto(userInForm.getPhoto());
        }

        userInDB.setFirstName(userInDB.getFirstName());
        userInDB.setLastName(userInDB.getLastName());

        return userRepo.save(userInDB);
    }
}
