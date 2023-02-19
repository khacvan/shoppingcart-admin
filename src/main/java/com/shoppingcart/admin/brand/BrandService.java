package com.shoppingcart.admin.brand;

import com.shoppingcart.admin.entity.Brand;
import com.shoppingcart.admin.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class BrandService {
    

    @Autowired
    private BrandRepository brandRepo;
    public static final int BRANDS_PER_PAGE = 10;



    public List<Brand> listAll() {
        return (List<Brand>) brandRepo.findAll(Sort.by("name").ascending());
    }

    public Page<Brand> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum -1,BRANDS_PER_PAGE, sort);

        if(keyword != null){
            return  brandRepo.findAll(keyword, pageable);
        }
        return brandRepo.findAll(pageable);
    }

    public Brand save(Brand brand) {
        return brandRepo.save(brand);
    }

}
