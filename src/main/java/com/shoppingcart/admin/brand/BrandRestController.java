package com.shoppingcart.admin.brand;

import com.shoppingcart.admin.entity.Brand;
import com.shoppingcart.admin.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BrandRestController {
    @Autowired
    private BrandService service;

    @GetMapping("/brands/{id}/categories")
    public List<CategoryDTO> listCategoriesByBrand(@PathVariable(name = "id") Integer brandId) throws BrandNotFoundException, BrandNotFoundRestException {
        List<CategoryDTO> listCategories = new ArrayList<>();
        try {
            Brand brand = service.get(brandId);
            Set<Category> categories = brand.getCategories();
            for(Category category : categories) {
                CategoryDTO dto = new CategoryDTO(category.getId());
                listCategories.add(dto);
            }
            return listCategories;
        }catch(BrandNotFoundException e) {
            throw new BrandNotFoundRestException();
        }
    }
}
