package main;

import model.Category;
import model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import repository.CategoryRepository;
import repository.ProductRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void saveProduct_andFindAll() {
        Category electronics = categoryRepository.save(new Category("ElectronicsTest"));
        Product product = new Product("Laptop", electronics, "Computers", 4.5);
        productRepository.save(product);

        List<Product> all = productRepository.findAll();
        assertEquals(1, all.size());
        assertEquals("Laptop", all.get(0).getProductName());
        assertEquals("ElectronicsTest", all.get(0).getCategory().getName());
        assertEquals("Computers", all.get(0).getSubCategory());
        assertEquals(4.5, all.get(0).getCustomerRating());
    }

    @Test
    void findByCategoryId() {
        Category electronics = categoryRepository.save(new Category("ElectronicsFindByCat"));
        Category fashion = categoryRepository.save(new Category("FashionFindByCat"));
        productRepository.save(new Product("Laptop", electronics, "Computers", 4.5));
        productRepository.save(new Product("Phone", electronics, "Mobile", 4.2));
        productRepository.save(new Product("Shirt", fashion, "Men", 4.0));

        List<Product> electronicsProducts = productRepository.findByCategory_Id(electronics.getId());
        assertEquals(2, electronicsProducts.size());
    }
}
