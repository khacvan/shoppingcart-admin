package com.shoppingcart.admin.category;

import com.shoppingcart.admin.FileUploadUtil;
import com.shoppingcart.admin.category.CategoryService;
import com.shoppingcart.admin.category.export.CategoryCsvExporter;
import com.shoppingcart.admin.category.export.CategoryExcelExporter;
import com.shoppingcart.admin.category.export.CategoryPdfExporter;
import com.shoppingcart.admin.entity.Category;
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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@Controller
public class CategoryController {


    @Autowired
    private CategoryService service;

//    @GetMapping("/categories")
//    public String listAll(Model model) {
//        List<Category> listCategories = service.listAll();
//        model.addAttribute("listCategories", listCategories);
//        return "categories/categories";
//    }

    @GetMapping("/categories/new")
    public String showForm(Model model){
        Category category = new Category();
        model.addAttribute("category",category);
        List<Category> listCategories = service.listCategoriesUsedInForm();
        model.addAttribute("listCategories", listCategories);


        model.addAttribute("pageTitle","Create New Category");
        return "categories/categories_form";

    }



    @PostMapping("/categories/save")
    public String saveCategory(Category cate, RedirectAttributes redirectAttributes, @RequestParam("image") MultipartFile multipartFile) throws IOException {

        if(!multipartFile.isEmpty()){
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            cate.setPhoto(fileName);
            Category savedCate = service.save(cate);
            String uploadDir = "category-photo/" + savedCate.getId();


            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName,multipartFile);

        }else {
            if(cate.getPhoto().isEmpty()){
                cate.setPhoto(null);
            }
            service.save(cate);

        }

        return "redirect:/categories";
    }

    @GetMapping("categories/edit/{id}")
    public String editCategory(Model model, @PathVariable(name="id")Integer id, RedirectAttributes redirectAttributes) {
        try {

            Category category = service.findById(id);
            model.addAttribute("category", category);
            model.addAttribute("pageTitle", "Edit category(ID: " +id+")");
            return "categories/categories_form";
        }
        catch(UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/categories";
        }
    }

    @GetMapping("categories/delete/{id}")
    public String deleteUser(Model model, @PathVariable(name="id")Integer id, RedirectAttributes redirectAttributes) {
        try {
            service.delete(id);
            String categoryPhotoDir = "category-photo/" + id;
            FileUploadUtil.removedir(categoryPhotoDir);


            redirectAttributes.addFlashAttribute("message", "Delete category " + id +" successfully");
            return "redirect:/categories";
        }
        catch(UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/categories";
        }
    }

    @GetMapping("categories/{id}/enabled/{enable}")
    public String enableUser(Model model, @PathVariable(name="id")Integer id, RedirectAttributes redirectAttributes, @PathVariable(name="enable")Boolean enable) {
        try {
            service.setEnable(id, enable);
            redirectAttributes.addFlashAttribute("message", enable==true ? "Enable category( ID: " +id +") successfully" : "Disable category( ID: " +id +") successfully");
            return "redirect:/categories";
        }
        catch(UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/categories";
        }
    }



    @GetMapping("/categories")
    public String listFirstPage(Model model) {
        return listByPage(1,model,"name","asc",null);
    }

//    @GetMapping("/categories")
//    public String listAll(Model model) {
//        List<Category> listCategories = service.listAll();
//        model.addAttribute("listCategories", listCategories);
//        return "categories/categories";
//    }

    @GetMapping("/categories/page/{pageNum}")
    public String listByPage(@PathVariable(name= "pageNum") int pageNum, Model model,
                             @Param("sortField") String sortField, @Param("sortDir") String sortDir,@Param("keyword") String keyword){
//
//        System.out.println("sortField "+ sortField);
//        System.out.println("sort Order "+ sortDir);
//
//        Page<Category> page = service.listByPage(pageNum, sortField, sortDir,keyword);
//        List<Category> listCategories= page.getContent();

        if(sortDir==null && sortDir.isEmpty()){
            sortDir="asc";
        }

        CategoryPageInfo pageInfo = new CategoryPageInfo();
        List<Category> listCategories =service.listByPage(pageInfo, pageNum, sortDir, keyword);

        long startCount = (pageNum -1) * CategoryService.ROOT_CATEGORIES_PER_PAGE + 1;
        long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE - 1;
        if(endCount > pageInfo.getTotalElements()){
            endCount = pageInfo.getTotalElements();
        }
        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", pageInfo.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", pageInfo.getTotalElements());
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);




        return "categories/categories";
    }

    @GetMapping("/categories/export/csv")
    public void exportToCsv(HttpServletResponse response) throws  IOException{
        List<Category> listCategories = service.listAll();
        CategoryCsvExporter exporter = new CategoryCsvExporter();
        exporter.export(listCategories, response);

    }


    @GetMapping("/categories/export/excel")
    public void exportToExcel(HttpServletResponse response) throws  IOException{
        List<Category> listCategories = service.listAll();
        CategoryExcelExporter exporter = new CategoryExcelExporter();
        exporter.export(listCategories, response);
    }

    @GetMapping("/categories/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws  IOException{
        List<Category> listCategories = service.listAll();
        CategoryPdfExporter exporter = new CategoryPdfExporter();
        exporter.export(listCategories, response);
    }





}
