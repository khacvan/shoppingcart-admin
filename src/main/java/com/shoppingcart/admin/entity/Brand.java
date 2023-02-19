package com.shoppingcart.admin.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;



@Entity
@Table(name = "brands")
public class Brand extends IdBaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 45)
    private String name;

    @Column(name = "logo", nullable = false, length = 128)
    private String logo;

    @ManyToMany
    @JoinTable(name = "brand_category",
            joinColumns = @JoinColumn(name = "brand_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public Brand() {}

    public Brand(String name, String logo) {
        this.name = name;
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }


    @Transient
    public String getPhotosImagePath(){
        if(id == null || logo == null){
            return "/images/default-user.png";
        }
        return  "/brand-logo/" + this.id + "/" + this.logo;

    }
}
