package com.shoppingcart.admin.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "categories")
public class Category  extends IdBaseEntity{
    @Column(length = 128, nullable = false, unique = true)
    private String name;

    @Column(length = 64, nullable = false)
    private String alias;

    @Column(length = 128)
    private String photo;

    private boolean enabled;

//    @Column(name = "all_parent_ids")
//    private String allParentIDs;


    @OneToOne
    @JoinColumn(name= "parent_id")
    private Category parent;
    @OneToMany(mappedBy = "parent")
    @OrderBy("name asc")
    private Set<Category> children = new HashSet<>();


    @Transient
    private boolean hasChildren;

    public boolean isHasChildren() {
        return hasChildren;
    }

//    public void setHasChildren(boolean hasChildren){
//        this.hasChildren = hasChildren;
//    }


    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public static Category copyIdAndname(Category category){
        Category copyCategor = new Category();
        copyCategor.setId(category.getId());
        copyCategor.setName(category.getName());

        return copyCategor;
    }
    public static Category copyIdAndname(Integer id, String name) {
        Category copyCategor = new Category();
        copyCategor.setId(id);
        copyCategor.setName(name);

        return copyCategor;

    }


    public static Category copyFull(Category category) {
        Category copyCategory = new Category();
        copyCategory.setId(category.getId());
        copyCategory.setName(category.getName());
        copyCategory.setPhoto(category.getPhoto());
        copyCategory.setAlias(category.getAlias());
        copyCategory.setEnabled(category.isEnabled());
        category.setHasChildren(category.getChildren().size()>0);

        return copyCategory;

    }

    public static Category copyFull(Category category, String name) {
        Category copyCategory = Category.copyFull(category);
        copyCategory.setName(name);


        return copyCategory;

    }
//    public String getAllParentIDs() {
//        return allParentIDs;
//    }
//
//    public void setAllParentIDs(String allParentIDs) {
//        this.allParentIDs = allParentIDs;
//    }

    public Category(String name, Category parent) {
        this(name);
        this.parent  = parent;
    }


    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public Set<Category> getChildren() {
        return children;
    }

    public void setChildren(Set<Category> children) {
        this.children = children;
    }

    public Category() {
    }

    public Category(String name, String alias, boolean enabled) {
        this.name = name;
        this.alias = alias;
        this.enabled = enabled;
    }

    public Category(String name) {
        this.name = name;
        this.alias = "kkk";
        this.photo ="default.png";
    }







    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Transient
    public String getPhotosImagePath(){
        if(id == null || photo == null){
            return "/images/default-user.png";
        }
        return  "/category-photo/" + this.id + "/" + this.photo;

    }

    @Override
    public String toString() {
        return "Category{" +
                "name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", image='" + photo + '\'' +
                ", enabled=" + enabled +
                '}';
    }


}
