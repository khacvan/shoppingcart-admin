package com.shoppingcart.admin.category;


import com.shoppingcart.admin.entity.Category;
import com.shoppingcart.admin.entity.User;
import com.shoppingcart.admin.user.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class CategoryService {

    public static final int CATEGORIES_PER_PAGE = 4;
    public static final int ROOT_CATEGORIES_PER_PAGE = 4;

    @Autowired
    private CategoryRepository categoryRepo;


    public List<Category> listAll(){

        return (List<Category>) categoryRepo.findAll(Sort.by("name").ascending());
    }

    public List<Category> listCategoriesUsedInForm(){
        List<Category> categoriesUsedInForm = new ArrayList<>();

        Iterable<Category> categoriesInDB = categoryRepo.findRootCategories(Sort.by("name").ascending());

        for (Category category: categoriesInDB){
            categoriesUsedInForm.add(Category.copyIdAndname(category));
            Set<Category> children = category.getChildren();

            for (Category subCategory: children){
                String name = "--" + subCategory.getName();
                categoriesUsedInForm.add(Category.copyIdAndname(subCategory.getId(),name));
                listSubCategoriesUsedInform(categoriesUsedInForm, subCategory, 1);
            }
        }

        return categoriesUsedInForm;

    }

    private void listSubCategoriesUsedInform(List<Category> categoriesUsedInForm, Category parent, int subLevel){
        int newSubLevel = subLevel + 1;
        Set<Category> children = (parent.getChildren());
        for (Category subCategory: children){
            String name = "";
            for (int i=0; i< newSubLevel; i++){
                name +="--";
            }
            name+= subCategory.getName();
            categoriesUsedInForm.add(Category.copyIdAndname(subCategory.getId(),name));
            listSubCategoriesUsedInform(categoriesUsedInForm,subCategory,newSubLevel);

        }
    }



    public Page<Category> listByPage(int pageNum, String sortField, String sortDir,String keyword){
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();



        Pageable pageable = PageRequest.of(pageNum -1,CATEGORIES_PER_PAGE, sort);
        if(keyword != null){
            return  categoryRepo.findAll(keyword, pageable);
        }
        return categoryRepo.findAll(pageable);
    }


    public List<Category> listByPage(CategoryPageInfo pageInfo,int pageNum, String sortDir,String keyword){
        Sort sort = Sort.by("name");
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();



        Pageable pageable = PageRequest.of(pageNum -1,ROOT_CATEGORIES_PER_PAGE  - 1, sort);

        Page<Category> pageCategories = null;

        if(keyword != null){
            pageCategories = categoryRepo.search(keyword, pageable);
        } else {
            pageCategories = categoryRepo.findRootCategories(pageable);
        }

        List<Category> rootCategories = pageCategories.getContent();

        pageInfo.setTotalElements(pageCategories.getTotalElements());
        pageInfo.setTotalPages(pageCategories.getTotalPages());

        if(keyword != null && !keyword.isEmpty()){
            List<Category> searchResult = pageCategories.getContent();
            for (Category category : searchResult){
                category.setHasChildren(category.getChildren().size()>0);
            }
            return searchResult;
        }else {
            return listHierarchicalCategories(rootCategories, sortDir);
        }
    }

    private List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortDir) {
        List<Category> hierarchicalCategories = new ArrayList<>();
        for (Category rootCategory : rootCategories){
            hierarchicalCategories.add(Category.copyFull(rootCategory));
            Set<Category> children = sortSubCategories(rootCategory.getChildren(),sortDir);

//            Set<Category> children = rootCategory.getChildren();

            for (Category subCategory: children){
                String name = "--" + subCategory.getName();
                hierarchicalCategories.add(Category.copyFull(subCategory, name));
                listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1);
            }
        }
        return  hierarchicalCategories;
    }

    private void listSubHierarchicalCategories(List<Category> hierarchicalCategories, Category parent, int subLevel) {
        Set<Category> children = parent.getChildren();
        int newSubLevel = subLevel + 1;

        for (Category subCategory: children){
            String name = "";
            for (int i = 0; i<newSubLevel; i++){
                name += "--";
            }
            name += subCategory.getName();

            hierarchicalCategories.add(Category.copyFull(subCategory, name));

            listSubCategoriesUsedInform(hierarchicalCategories, subCategory, newSubLevel);
        }
    }


    public Category findById(int id) throws UserNotFoundException {
        try {
            return this.categoryRepo.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new UserNotFoundException("Could not find any user with ID:" + id);
        }
    }

        public void setEnable(int id, boolean enable) throws UserNotFoundException {
        Category category = categoryRepo.findById(id).get();
        if(category == null) {
            throw new UserNotFoundException("Could not find any user with ID: " +id);
        }
        category.setEnabled(enable);
        categoryRepo.save(category);
    }

    public void setEnableByQuery(int id, boolean enable) {
        categoryRepo.updateCategoryEnableById(id, enable);
    }

    public Category save(Category category) {
        return categoryRepo.save(category);
    }

    public void delete(int id) throws UserNotFoundException {
        int countById = categoryRepo.countById(id);
        if(countById == 0) {
            throw new UserNotFoundException("Could not find any category with ID: " +id);
        }
        this.categoryRepo.deleteById(id);
    }


    private SortedSet<Category> sortSubCategories(Set<Category> children){
        return sortSubCategories(children,"asc");
    }


    private SortedSet<Category> sortSubCategories(Set<Category> children,String sortDir){
        SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
            @Override
            public int compare(Category cat1, Category cat2) {
                if(sortDir.equals("asc")){
                    return cat1.getName().compareTo(cat2.getName());
                }else {
                    return cat2.getName().compareTo(cat2.getName());
                }
            }
        });

        sortedChildren.addAll(children);
        return  sortedChildren;
    }




    public String checkUnique(Integer id, String name, String alias){
        boolean isCreatingNew = (id==null||id==0);

        Category categoryByName = categoryRepo.findByName(name);

        if(isCreatingNew){
            if(categoryByName != null){
                return "DuplicateName";
            }else {
                Category categoryByAlias = categoryRepo.findByAlias(alias);
                if(categoryByAlias != null){
                    return "DuplicateAlias";
                }
            }
        }else {
            if(categoryByName != null && categoryByName.getId() != id){
                return "DuplicateName";
            }

            Category categoryByAlias = categoryRepo.findByAlias(alias);
            if(categoryByAlias != null){
                return "DuplicateAlias";
            }
        }

        return "OK";
    }


}
