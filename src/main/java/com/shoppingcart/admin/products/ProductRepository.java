package com.shoppingcart.admin.products;

import com.shoppingcart.admin.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductRepository extends PagingAndSortingRepository<Product,Integer> {


    @Query("SELECT u from Product u where concat(u.id, ' ',u.name) " + "LIKE %?1%")
    public Page<Product> findAll(String keyword, Pageable pageable);

}
