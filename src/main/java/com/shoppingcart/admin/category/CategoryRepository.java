package com.shoppingcart.admin.category;




import com.shoppingcart.admin.entity.Category;
import com.shoppingcart.admin.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.Iterator;
import java.util.List;

public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer> {


    @Query("select c from Category c where c.name =:name")
    public  Category getCategoryByName(@Param("name")String name);

    public int countById(int id);

    @Query("UPDATE Category c SET c.enabled=?2 WHERE c.id=?1")
    @Modifying
    public void updateCategoryEnableById(int id, boolean enable);
    public Long countById(Integer id);



    @Query("select c from Category c where c.parent.id is NULL")
    public List<Category> findRootCategories(Sort sort);
    @Query("select c from Category c where c.parent.id is NULL")
    public Page<Category> findRootCategories(Pageable pageable);



    @Query("select c from Category c where c.parent.id is NULL")
    public Page<Category> findRootCategories(String keyword, Pageable pageable);

    @Query("SELECT u from  Category u where u.alias like %?1% or u.name like %?1%")
    public Page<Category> findAll(String keyword, Pageable pageable);


    @Query("select c from Category c where  c.parent.id is NULL")
    public Page<Category> search(Pageable pageable);

    @Query("select c from Category c where  c.name like %?1%")
    public Page<Category> search(String keyword, Pageable pageable);


    @Query("select c from Category c where  c.name like %?1%")
    public Category findByName(String name);
    @Query("select c from Category c where  c.alias like %?1%")
    public Category findByAlias(String alias);
}
