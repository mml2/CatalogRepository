package controller;

import dto.CategoryResponse;
import dto.ProductResponse;
import model.Category;
import model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import services.CategoryService;
import services.ProductService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Create a product (admin only)")
    public ResponseEntity<ProductResponse> addProduct(@RequestBody ProductRequest request) {
        Category category = categoryService.getCategoryById(request.getCategoryId());
        Product product = new Product(
                request.getProductName(),
                category,
                request.getSubCategory(),
                request.getCustomerRating()
        );
        Product saved = productService.addProduct(product);
        log.info("Product created: id={}, name={}", saved.getId(), saved.getProductName());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @io.swagger.v3.oas.annotations.Operation(summary = "List products, optionally by category")
    public List<ProductResponse> getProducts(@RequestParam(required = false) Long categoryId) {
        List<Product> products = categoryId != null
                ? productService.getProductsByCategoryId(categoryId)
                : productService.getAllProducts();
        log.info("Returning {} product(s) for categoryId={}", products.size(), categoryId);
        return products.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private ProductResponse toResponse(Product p) {
        Category c = p.getCategory();
        CategoryResponse categoryResponse = c != null ? new CategoryResponse(c.getId(), c.getName()) : null;
        return new ProductResponse(
                p.getId(),
                p.getProductName(),
                categoryResponse,
                p.getSubCategory(),
                p.getCustomerRating()
        );
    }

    /**
     * DTO for receiving product info from the client.
     */
    public static class ProductRequest {
        private String productName;
        private Long categoryId;
        private String subCategory;
        private Double customerRating;

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public Long getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Long categoryId) {
            this.categoryId = categoryId;
        }

        public String getSubCategory() {
            return subCategory;
        }

        public void setSubCategory(String subCategory) {
            this.subCategory = subCategory;
        }

        public Double getCustomerRating() {
            return customerRating;
        }

        public void setCustomerRating(Double customerRating) {
            this.customerRating = customerRating;
        }
    }
}
