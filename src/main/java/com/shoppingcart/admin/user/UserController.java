package com.shoppingcart.admin.user;

import com.shoppingcart.admin.FileUploadUtil;
import com.shoppingcart.admin.entity.Role;
import com.shoppingcart.admin.entity.User;
import com.shoppingcart.admin.user.UserNotFoundException;
import com.shoppingcart.admin.user.UserService;
import com.shoppingcart.admin.user.export.UserCsvExporter;
import com.shoppingcart.admin.user.export.UserExcelExporter;
import com.shoppingcart.admin.user.export.UserPdfExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService service;

    private String defaultRedirectURL ="redirect:/users/page/1?sortField=firstName&sortDir=asc";



    @GetMapping("/users/new")
    public String showForm(Model model){
        User user = new User();
        model.addAttribute("user",user);
        List<Role> listRoles = service.listRoles();
        model.addAttribute("listRoles", listRoles);
        return "users/users_form";

    }
    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes, @RequestParam("image") MultipartFile multipartFile) throws IOException {
        if(!multipartFile.isEmpty()){
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhoto(fileName);
            User savedUser = service.save(user);
            String uploadDir = "user-photo/" + savedUser.getId();


            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName,multipartFile);

        }else {
            if(user.getPhoto().isEmpty()){
                user.setPhoto(null);
            }
            service.save(user);

        }


        redirectAttributes.addFlashAttribute("message","The user has been save successfully");



        return getRedirectURLtoAffectedUser(user);
    }

    private String getRedirectURLtoAffectedUser(User user){
        String firstPartOfEmail = user.getEmail().split("@")[0];
        return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPartOfEmail;
    }

    @GetMapping("users/edit/{id}")
    public String editUser(Model model, @PathVariable(name="id")Integer id, RedirectAttributes redirectAttributes) {
        try {
            List<Role> listRoles = service.listRoles();
            model.addAttribute("listRoles", listRoles);
            User user = service.findById(id);
            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Edit user(ID: " +id+")");
            return "users/users_form";
        }
        catch(UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return defaultRedirectURL;
        }
    }

    @GetMapping("users/delete/{id}")
    public String deleteUser(Model model, @PathVariable(name="id")Integer id, RedirectAttributes redirectAttributes) {
        try {
            service.delete(id);
            String userPhotoDir = "user-photo/" + id;
            FileUploadUtil.removedir(userPhotoDir);


            redirectAttributes.addFlashAttribute("message", "Delete user " + id +" successfully");
            return "redirect:/users";
        }
        catch(UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return defaultRedirectURL;
        }
    }

    @GetMapping("users/{id}/enabled/{enable}")
    public String enableUser(Model model, @PathVariable(name="id")Integer id, RedirectAttributes redirectAttributes, @PathVariable(name="enable")Boolean enable) {
        try {
            service.setEnable(id, enable);
            redirectAttributes.addFlashAttribute("message", enable==true ? "Enable user( ID: " +id +") successfully" : "Disable user( ID: " +id +") successfully");
            return "redirect:/users";
        }
        catch(UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/users";
        }
    }

    @GetMapping("/users")
    public String listFirstPage(Model model) {
//        List<User> listUsers = service.listAll();
//        model.addAttribute("listUsers", listUsers);
//        return "redirect:/users/page/1";

        return defaultRedirectURL;
    }

    @GetMapping("/users/page/{pageNum}")
    public String listByPage(@PathVariable(name= "pageNum") int pageNum, Model model,
                             @Param("sortField") String sortField, @Param("sortDir") String sortDir,
                             @Param("keyword") String keyword){

        System.out.println("sortField "+ sortField);
        System.out.println("sort Order "+ sortDir);

        Page<User> page = service.listByPage(pageNum, sortField, sortDir, keyword);
        List<User> listUsers = page.getContent();

        long startCount = (pageNum -1) * UserService.USERS_PER_PAGE + 1;
        long endCount = startCount + UserService.USERS_PER_PAGE - 1;
        if(endCount > page.getTotalElements()){
            endCount = page.getTotalElements();
        }
        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listUsers", listUsers);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);




        return "users/users";
    }

    @GetMapping("/users/export/csv")
    public void exportToCsv(HttpServletResponse response) throws  IOException{
        List<User> listUsers = service.listAll();
        UserCsvExporter exporter = new UserCsvExporter();
        exporter.export(listUsers, response);

    }


    @GetMapping("/users/export/excel")
    public void exportToExcel(HttpServletResponse response) throws  IOException{
        List<User> listUsers = service.listAll();

        UserExcelExporter exporter = new UserExcelExporter();
        exporter.export(listUsers, response);

    }

    @GetMapping("/users/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws  IOException{
        List<User> listUsers = service.listAll();

        UserPdfExporter exporter = new UserPdfExporter();
        exporter.export(listUsers, response);

    }



}
