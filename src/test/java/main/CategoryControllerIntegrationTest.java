package main;

import dto.CategoryResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import repository.CategoryRepository;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void addCategory_andGetAllCategories_persistsToDb() {
        ResponseEntity<CategoryResponse> createResponse = restTemplate
                .withBasicAuth("admin", "admin")
                .postForEntity("/api/categories", Map.of("name", "Home & Garden"), CategoryResponse.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        CategoryResponse created = createResponse.getBody();
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Home & Garden");

        // Verify category persisted in DB
        assertThat(categoryRepository.findById(created.getId())).isPresent();

        // Get all categories via API (authenticated)
        ResponseEntity<List<CategoryResponse>> listResponse = restTemplate
                .withBasicAuth("user", "user")
                .exchange("/api/categories", HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<CategoryResponse>>() {});

        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResponse.getBody()).isNotNull();
        assertThat(listResponse.getBody()).anyMatch(c ->
                "Home & Garden".equals(c.getName()) && c.getId().equals(created.getId()));
    }

    @Test
    void getCategories_unauthorized_returns401() {
        ResponseEntity<List<?>> response = restTemplate.exchange("/api/categories", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<?>>() {});
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void addCategory_asUser_returns403() {
        ResponseEntity<CategoryResponse> response = restTemplate
                .withBasicAuth("user", "user")
                .postForEntity("/api/categories", Map.of("name", "Forbidden"), CategoryResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
