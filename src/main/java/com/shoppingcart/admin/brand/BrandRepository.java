package com.shoppingcart.admin.brand;

import com.shoppingcart.admin.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface BrandRepository extends PagingAndSortingRepository<Brand,Integer> {
    @Query("SELECT u from Brand u where concat(u.id, ' ',u.name) " + "LIKE %?1%")
    public Page<Brand> findAll(String keyword, Pageable pageable);

    @Query("SELECT u FROM Brand u WHERE u.name = :name")
    public Brand getBrandByName(@Param("name")String name);


    public Long countById(Integer id);

    public Brand findByName(String name);
}
