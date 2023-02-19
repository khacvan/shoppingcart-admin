package com.shoppingcart.admin.products;

import com.shoppingcart.admin.entity.Brand;
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
public class ProductService {
    

    @Autowired
    private ProductRepository productRepository;
    public static final int PRODUCT_PER_PAGE = 10;



    public List<Product> listAll() {
        return (List<Product>) productRepository.findAll(Sort.by("name").ascending());
    }

    public Page<Product> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum -1,PRODUCT_PER_PAGE, sort);

        if(keyword != null){
            return  productRepository.findAll(keyword, pageable);
        }
        return productRepository.findAll(pageable);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

}
