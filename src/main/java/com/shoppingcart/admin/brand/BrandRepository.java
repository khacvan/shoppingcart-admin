package com.shoppingcart.admin.brand;

import com.shoppingcart.admin.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BrandRepository extends PagingAndSortingRepository<Brand,Integer> {
    @Query("SELECT u from Brand u where concat(u.id, ' ',u.name) " + "LIKE %?1%")
    public Page<Brand> findAll(String keyword, Pageable pageable);

}
