package com.shoppingcart.admin.brand;

import com.shoppingcart.admin.FileUploadUtil;
import com.shoppingcart.admin.brand.export.BrandCsvExporter;
import com.shoppingcart.admin.brand.export.BrandExcelExporter;
import com.shoppingcart.admin.brand.export.BrandPdfExporter;
import com.shoppingcart.admin.category.CategoryService;
import com.shoppingcart.admin.entity.Brand;
import com.shoppingcart.admin.entity.Category;
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
public class BrandController {
    @Autowired
    private BrandService service;

    @Autowired
    private CategoryService serviceCat;

    private String defaultRedirectURL ="redirect:/brands/page/1?sortField=name&sortDir=asc";
    @GetMapping("/brands/new")
    public String showForm(Model model){
        Brand brand = new Brand();
        model.addAttribute("brand",brand);
        List<Category> listCategories = serviceCat.listCategoriesUsedInForm();
        model.addAttribute("listCategories", listCategories);


        model.addAttribute("pageTitle","Create New Brand");
        return "brands/brands_form";

    }



    @GetMapping("/brands")
    public String listFirstPage(Model model) {
//        List<User> listBrands = service.listAll();
//        model.addAttribute("listBrands", listBrands);
//        return "redirect:/users/page/1";

        return defaultRedirectURL;
    }


    @PostMapping("/brands/save")
    public String saveCategory(Brand brand, RedirectAttributes redirectAttributes, @RequestParam("image") MultipartFile multipartFile) throws IOException {

        if(!multipartFile.isEmpty()){
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            brand.setLogo(fileName);
            Brand saveBrand = service.save(brand);
            String uploadDir = "brand-logo/" + saveBrand.getId();


            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName,multipartFile);

        }else {
            if(brand.getLogo().isEmpty()){
                brand.setLogo(null);
            }
            service.save(brand);

        }

        return "redirect:/brands";
    }


    @GetMapping("/brands/page/{pageNum}")
    public String listByPage(@PathVariable(name= "pageNum") int pageNum, Model model,
                             @Param("sortField") String sortField, @Param("sortDir") String sortDir,
                             @Param("keyword") String keyword){

        System.out.println("sortField "+ sortField);
        System.out.println("sort Order "+ sortDir);

        Page<Brand> page = service.listByPage(pageNum, sortField, sortDir, keyword);
        List<Brand> listBrands = page.getContent();

        long startCount = (pageNum -1) * BrandService.BRANDS_PER_PAGE + 1;
        long endCount = startCount + BrandService.BRANDS_PER_PAGE - 1;
        if(endCount > page.getTotalElements()){
            endCount = page.getTotalElements();
        }
        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listBrands", listBrands);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);




        return "brands/brands";
    }

    @GetMapping("/brands/export/csv")
    public void exportToCsv(HttpServletResponse response) throws  IOException{
        List<Brand> listBrands = service.listAll();
        BrandCsvExporter exporter = new BrandCsvExporter();
        exporter.export(listBrands, response);

    }


    @GetMapping("/brands/export/excel")
    public void exportToExcel(HttpServletResponse response) throws  IOException{
        List<Brand> listBrands = service.listAll();

        BrandExcelExporter exporter = new BrandExcelExporter();
        exporter.export(listBrands, response);

    }

    @GetMapping("/brands/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws  IOException{
        List<Brand> listBrands = service.listAll();

        BrandPdfExporter exporter = new BrandPdfExporter();
        exporter.export(listBrands, response);

    }


}
