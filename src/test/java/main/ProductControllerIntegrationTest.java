package main;

import dto.CategoryResponse;
import dto.ProductResponse;
import model.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import repository.CategoryRepository;
import repository.ProductRepository;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void addProduct_andGetProducts_persistsToDb() {
        // Create category via API (admin)
        ResponseEntity<CategoryResponse> categoryResponse = restTemplate
                .withBasicAuth("admin", "admin")
                .postForEntity("/api/categories", Map.of("name", "Toys"), CategoryResponse.class);
        assertThat(categoryResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        CategoryResponse category = categoryResponse.getBody();
        assertThat(category).isNotNull();
        assertThat(category.getId()).isNotNull();
        assertThat(category.getName()).isEqualTo("Toys");

        // Verify category in DB
        assertThat(categoryRepository.findById(category.getId())).isPresent();

        // Add product via API (admin)
        Map<String, Object> request = Map.of(
                "productName", "Action Figure",
                "categoryId", category.getId(),
                "subCategory", "Collectibles",
                "customerRating", 4.8
        );

        ResponseEntity<ProductResponse> productResponse = restTemplate
                .withBasicAuth("admin", "admin")
                .postForEntity("/api/products", request, ProductResponse.class);
        assertThat(productResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ProductResponse product = productResponse.getBody();
        assertThat(product).isNotNull();
        assertThat(product.getId()).isNotNull();
        assertThat(product.getProductName()).isEqualTo("Action Figure");
        assertThat(product.getCategory()).isNotNull();
        assertThat(product.getCategory().getName()).isEqualTo("Toys");
        assertThat(product.getSubCategory()).isEqualTo("Collectibles");
        assertThat(product.getCustomerRating()).isEqualTo(4.8);

        // Verify product in DB
        assertThat(productRepository.findById(product.getId())).isPresent();
        assertThat(productRepository.findById(product.getId()).orElseThrow().getCategory().getId()).isEqualTo(category.getId());

        // Get all products via API (authenticated user)
        ResponseEntity<List<ProductResponse>> listResponse = restTemplate
                .withBasicAuth("user", "user")
                .exchange("/api/products", HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<ProductResponse>>() {});
        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResponse.getBody()).hasSizeGreaterThanOrEqualTo(1);
        assertThat(listResponse.getBody()).anyMatch(p ->
                "Action Figure".equals(p.getProductName()) && p.getCategory().getName().equals("Toys"));

        // Get products by categoryId via API
        ResponseEntity<List<ProductResponse>> byCategoryResponse = restTemplate
                .withBasicAuth("user", "user")
                .exchange("/api/products?categoryId=" + category.getId(), HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<ProductResponse>>() {});
        assertThat(byCategoryResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(byCategoryResponse.getBody()).hasSize(1);
        assertThat(byCategoryResponse.getBody().get(0).getProductName()).isEqualTo("Action Figure");
    }

    @Test
    void getProducts_unauthorized_returns401() {
        ResponseEntity<List<?>> response = restTemplate.exchange("/api/products", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<?>>() {});
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void addProduct_asUser_returns403() {
        Category category = categoryRepository.save(new Category("TestCategory"));
        Map<String, Object> request = Map.of(
                "productName", "Forbidden Product",
                "categoryId", category.getId(),
                "subCategory", "Test",
                "customerRating", 3.0
        );

        ResponseEntity<ProductResponse> response = restTemplate
                .withBasicAuth("user", "user")
                .postForEntity("/api/products", request, ProductResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
