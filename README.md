## Spring Boot Product & Category API

This project is a **Spring Boot 3.2 / Java 17** REST API for managing **products** and **categories**, built with a clean layered architecture (controller → service → repository) and DTO-based responses.

### Tech Stack

- **Backend**: Spring Boot 3.2 (Web, Data JPA, Validation, Security)
- **Database**: In-memory H2
- **Build**: Maven
- **Docs**: springdoc OpenAPI (Swagger UI)
- **Tests**: JUnit 5, Spring Boot test support

### Getting Started

#### Prerequisites

- Java 17+
- Maven 3.8+

#### Run the application

```bash
mvn spring-boot:run
```

The app will start on `http://localhost:8080`.

### API Documentation

After the app is running, open:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`

All REST endpoints are documented there with request/response schemas.

### Architecture Overview

- `controller` – `@RestController` endpoints, returning `ResponseEntity` and using request/response DTOs.
- `services` – business logic, transaction boundaries.
- `repository` – Spring Data JPA repositories for `Category` and `Product`.
- `model` – JPA entities (never exposed directly by controllers).
- `main.config` – security configuration, data loader, and global exception handling.

### Security

- Spring Security is enabled via `SecurityConfig`.
- Method-level authorization uses `@PreAuthorize` where needed.
- Use the configured users/roles in `SecurityConfig` (or your own configuration) when calling protected endpoints.

### Database

- Uses **H2 in-memory** database by default (see `src/main/resources/application.properties`).
- Data is initialized via `CategoryDataLoader` on application startup.

### Testing

Run all tests with:

```bash
mvn test
```

There are integration tests for controllers and repository tests for data access.

