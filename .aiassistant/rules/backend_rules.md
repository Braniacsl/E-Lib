---
apply: always
---

# Backend Rules (Java/Spring Boot)

## Architectural Enforcement
Strict Layered Architecture required:
1. Controller: @RestController. HTTP handling only. Validates DTOs. NEVER contains business logic. NEVER calls Repository
2. Service: @Service. transactional business logic. Transforms Entity <-> DTO
3. Repository: @Repository. JpaRepository interfaces only
4. Domain: @Entity. JPA definitions. Rich domain models preferred
5. DTO: Records (Java 21). Request/Response structures. NEVER return Entity from Controller

## Code Style & Quality
- Java 21 features: Use Records, Pattern Matching, Switch expressions, Virtual Threads where applicable
- Functional approach: Use Stream API for collections processing. Avoid deep nesting loops
- Spring Nativity:
    - Use Constructor Injection (@RequiredArgsConstructor)
    - Use @Transactional for data mutation
    - Avoid @Autowired on fields
- Linter Compliance: No unused imports. No raw types. No public fields in classes (use Lombok)
- Error Handling:
    - Throw custom runtime exceptions
    - Global @ControllerAdvice maps exceptions to standard error JSON
    - Never swallow exceptions

## Testing
- JUnit 5 + Mockito
- Service Layer: Unit tests with mocked Repositories
- Controller Layer: MockMvc tests using @WebMvcTest
- Integration: @SpringBootTest with Testcontainers (Postgres)