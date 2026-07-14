# Step 5: Unit & API Tests

## Test Structure

```
backend/src/test/java/com/company/ems/
├── support/
│   └── TestDataFactory.java       # Shared test data
├── service/
│   ├── AuthServiceTest.java       # Mockito unit tests (3 tests)
│   └── EmployeeServiceTest.java   # Mockito unit tests (9 tests)
├── controller/
│   ├── AuthControllerTest.java    # MockMvc API tests (3 tests)
│   └── EmployeeControllerTest.java# MockMvc API tests (8 tests)
└── integration/
    └── EmployeeApiIntegrationTest.java  # Full stack tests (9 tests)
```

**Total: 32 automated tests**

## Run Tests

```powershell
cd c:\Devops\backend
mvn clean test
```

## Run by Layer

```powershell
# Service unit tests only
mvn test -Dtest="*ServiceTest"

# Controller API tests only
mvn test -Dtest="*ControllerTest"

# Integration tests only
mvn test -Dtest="*IntegrationTest"
```

## Coverage Report

After `mvn test`, open:
```
backend/target/site/jacoco/index.html
```

## Test Layers Explained

| Layer | Annotation | What it tests |
|-------|------------|---------------|
| Service | `@ExtendWith(MockitoExtension.class)` | Business logic in isolation |
| Controller | `@WebMvcTest` + `MockMvc` | HTTP status codes, JSON responses |
| Integration | `@SpringBootTest` + `MockMvc` | Full API flow with real H2 DB |
