package com.shoppingcart.admin.categories;

import com.shoppingcart.admin.category.CategoryRepository;
import com.shoppingcart.admin.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Set;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class testCategory {


    @Autowired
    private CategoryRepository repo;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateRootCategory(){
        Category computers = new Category ("Computers");//id=1

        Category desktops = new Category("Desktops", computers);

        Category laptops = new Category("Laptops", computers);

        Category laptop1 = new Category("Laptop 1", laptops);

        Category laptop2 = new Category("Laptop 2", laptops);

        Category laptop21 = new Category("Laptop 21", laptop2); Category laptop211 = new Category("Laptop 211", laptop21);

        Category laptop3= new Category("Laptop 3", laptops);

        Category computerComponents = new Category("Computer components", computers);

        Category memoryA = new Category ("Memory a", computerComponents);

        Category a1 = new Category("a1", memoryA); Category a2 = new Category ("a2", a1);

        Category a3 = new Category("a3", a2);

        Category a4 = new Category("a4", a3); Category memoryB = new Category("Memory b", computerComponents);

        Category b1 = new Category("b1", memoryB);

        Category b2 = new Category("b2", b1);

        repo.saveAll(List.of(computers, desktops, laptops, laptop1, laptop2, laptop21, laptop211, laptop3, computerComponents,
                memoryA, a1, a2, a3, a4, memoryB, b1, b2));


    }


    @Test
    public void callPrintRelationship(){
        Category category= entityManager.find(Category.class,1);
        System.out.println(category.getName());
        printfRelationship(category.getChildren(),"--");
    }

    @Test
    public void printfRelationship(Set<Category> listCategories, String underline){


        for(Category category: listCategories){
            System.out.print(underline);
            System.out.println(category.getName());

            if(category.getChildren()!=null){
                printfRelationship(category.getChildren(),underline+"--");
            }
        }



    }


}
