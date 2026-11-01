# expense-tracker-api

REST API for personal expense tracking built with Java 21, Spring Boot, JPA and PostgreSQL. Layered architecture (Controller/Service/Repository), DTO validation, global error handling, Docker Compose and automated tests.

API REST para controle de despesas pessoais com Java 21, Spring Boot, JPA e PostgreSQL.

## Stack

- Java 21, Spring Boot 3.5, Maven (wrapper included)
- Spring Web, Spring Data JPA, Bean Validation
- PostgreSQL 16 (Docker Compose)
- JUnit 5, Mockito, MockMvc, H2 (tests)

## Architecture

```
controller  ->  service  ->  repository  ->  PostgreSQL
   |               |
  DTOs        business rules
(validation)
```

| Package | Responsibility |
|---|---|
| `controller` | HTTP layer, request/response mapping |
| `service` | Business logic and transactions |
| `repository` | Spring Data JPA access |
| `dto` | `ExpenseRequest` (validated input) and `ExpenseResponse` |
| `model` | `Expense` entity and `Category` enum |
| `exception` | Domain exceptions and `GlobalExceptionHandler` (RFC 7807) |

## Running

Requirements: Java 21 and Docker.

```bash
docker compose up -d
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`. Database settings can be overridden with `DB_URL`, `DB_USER` and `DB_PASSWORD`.

## Endpoints

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/expenses` | Create an expense (`201` + `Location`) |
| `GET` | `/api/expenses/{id}` | Get one expense (`404` if missing) |
| `GET` | `/api/expenses?category=FOOD` | List expenses, newest first, optional category filter |

Categories: `FOOD`, `TRANSPORT`, `HOUSING`, `HEALTH`, `EDUCATION`, `LEISURE`, `OTHER`.

### Example

```bash
curl -i -X POST http://localhost:8080/api/expenses \
  -H "Content-Type: application/json" \
  -d '{"description":"Supermercado","amount":150.75,"category":"FOOD","date":"2026-10-15"}'
```

Validation errors return `400` with the failing fields:

```json
{
  "title": "Invalid request",
  "status": 400,
  "detail": "Validation failed",
  "errors": { "amount": "must be greater than zero" }
}
```

## Tests

```bash
./mvnw test
```

- `ExpenseControllerTest`: `@WebMvcTest` covering `POST`, validation errors and `404`
- `ExpenseRepositoryTest`: `@DataJpaTest` against in-memory H2 (PostgreSQL mode)
- `ExpenseServiceTest`: unit tests with Mockito

## Roadmap

- Totals by category and by month
- `PUT`/`DELETE` and pagination
- Flyway migrations
- Repository tests with Testcontainers
- OpenAPI/Swagger documentation
