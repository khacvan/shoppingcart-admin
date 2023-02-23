package com.shoppingcart.admin.products;

import com.shoppingcart.admin.FileUploadUtil;
import com.shoppingcart.admin.brand.BrandService;
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
public class ProductController {
    @Autowired
    private ProductService service;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService serviceCat;

    private String defaultRedirectURL ="redirect:/products/page/1?sortField=name&sortDir=asc";


    @GetMapping("/products/new")
    public String createNew(Model model) {
        List<Brand> listBrands = brandService.listAll();
        List<Category> listCategories = brandService.listCategories();
        Product product = new Product();
        product.setEnabled(true);
        product.setInStock(true);

        model.addAttribute("listBrands", listBrands);
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("product", product);
        model.addAttribute("pageTitle", "Create Product");
        model.addAttribute("numberofExisringExtraimages",0);

        return "products/product_form";
    }

    @PostMapping("/products/save")
    public String saveProduct (Product product, Model model, RedirectAttributes redirectAttributes,
                               @RequestParam("image") MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename()); // Amina Elshal.png--> Amina Elshal.png
            product.setMainImage(fileName);
            Product savedProduct = service.save(product);

            String uploadDir = "product-mainImages/" + savedProduct.getId(); // tạo folder user-photos theo id để lưu hình

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir,fileName, multipartFile);
        }
        else {
            if (product.getMainImage()==null || product.getMainImage().isEmpty()) product.setMainImage(null); { // nếu ko chọn image thì lưu null
                service.save(product);
            }
        }

        redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");

        return "redirect:/products";
    }
//    @GetMapping("/products/new")
//    public String showForm(Model model){
//        Product product = new Product();
//        model.addAttribute("Product",product);
//        List<Category> listCategories = serviceCat.listCategoriesUsedInForm();
//        model.addAttribute("listCategories", listCategories);
//
//
//        model.addAttribute("pageTitle","Create New Product");
//        return "products/products_form";
//
//    }



    @GetMapping("/products")
    public String listFirstPage(Model model) {
//        List<User> listproducts = service.listAll();
//        model.addAttribute("listproducts", listproducts);
//        return "redirect:/users/page/1";

        return defaultRedirectURL;
    }


//    @PostMapping("/products/save")
//    public String saveCategory(Product product, RedirectAttributes redirectAttributes, @RequestParam("image") MultipartFile multipartFile) throws IOException {
//
//        if(!multipartFile.isEmpty()){
//            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
//            product.setMainImage(fileName);
//            Product saveProduct = service.save(product);
//            String uploadDir = "product-images/" + saveProduct.getId();
//
//
//            FileUploadUtil.cleanDir(uploadDir);
//            FileUploadUtil.saveFile(uploadDir, fileName,multipartFile);
//
//        }else {
//            if(product.getMainImage().isEmpty()){
//                product.setMainImage(null);
//            }
//            service.save(product);
//
//        }
//
//        return "redirect:/products";
//    }


    @GetMapping("/products/page/{pageNum}")
    public String listByPage(@PathVariable(name= "pageNum") int pageNum, Model model,
                             @Param("sortField") String sortField, @Param("sortDir") String sortDir,
                             @Param("keyword") String keyword){

        System.out.println("sortField "+ sortField);
        System.out.println("sort Order "+ sortDir);

        Page<Product> page = service.listByPage(pageNum, sortField, sortDir, keyword);
        List<Product> listProducts = page.getContent();

        List<Brand> listBrands = brandService.listAll();
        List<Category> listCategories = serviceCat.listAll();


        long startCount = (pageNum -1) * ProductService.PRODUCT_PER_PAGE + 1;
        long endCount = startCount + ProductService.PRODUCT_PER_PAGE - 1;
        if(endCount > page.getTotalElements()){
            endCount = page.getTotalElements();
        }
        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listProducts", listProducts);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("listBrands", listBrands);
        model.addAttribute("listCategories", listCategories);



        return "products/products";
    }
//
//    @GetMapping("/products/export/csv")
//    public void exportToCsv(HttpServletResponse response) throws  IOException{
//        List<Product> listproducts = service.listAll();
//        ProductCsvExporter exporter = new ProductCsvExporter();
//        exporter.export(listproducts, response);
//
//    }
//
//
//    @GetMapping("/products/export/excel")
//    public void exportToExcel(HttpServletResponse response) throws  IOException{
//        List<Product> listproducts = service.listAll();
//
//        ProductExcelExporter exporter = new ProductExcelExporter();
//        exporter.export(listproducts, response);
//
//    }
//
//    @GetMapping("/products/export/pdf")
//    public void exportToPDF(HttpServletResponse response) throws  IOException{
//        List<Product> listproducts = service.listAll();
//
//        ProductPdfExporter exporter = new ProductPdfExporter();
//        exporter.export(listproducts, response);
//
//    }


}
