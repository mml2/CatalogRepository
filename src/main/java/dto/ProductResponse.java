package dto;

public class ProductResponse {

    private Long id;
    private String productName;
    private CategoryResponse category;
    private String subCategory;
    private Double customerRating;

    public ProductResponse() {
    }

    public ProductResponse(Long id, String productName, CategoryResponse category,
                           String subCategory, Double customerRating) {
        this.id = id;
        this.productName = productName;
        this.category = category;
        this.subCategory = subCategory;
        this.customerRating = customerRating;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public CategoryResponse getCategory() {
        return category;
    }

    public void setCategory(CategoryResponse category) {
        this.category = category;
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
