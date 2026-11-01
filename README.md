# expense-tracker-api

A REST API for tracking personal expenses, built with Java 21, Spring Boot, JPA and PostgreSQL. I built it as a portfolio project to practice a full backend slice: layered code, validated DTOs, global error handling, a database in Docker Compose and tests.

API REST para controle de despesas pessoais, feita com Java 21, Spring Boot, JPA e PostgreSQL.

## Stack

- Java 21, Spring Boot 3.5, Maven (the wrapper is included)
- Spring Web, Spring Data JPA, Bean Validation
- PostgreSQL 16 through Docker Compose
- JUnit 5, Mockito, MockMvc and H2 for the tests

## How the code is organized

```
controller  ->  service  ->  repository  ->  PostgreSQL
```

- `controller` maps HTTP requests to service calls.
- `service` holds the business logic and the transactions.
- `repository` is the Spring Data JPA interface.
- `dto` has `ExpenseRequest`, which carries the validation rules, and `ExpenseResponse`.
- `model` has the `Expense` entity and the `Category` enum.
- `exception` has the domain exceptions and a `GlobalExceptionHandler` that returns RFC 7807 problem details.

## Running it

You need Java 21 and Docker.

```bash
docker compose up -d
./mvnw spring-boot:run
```

The API listens on `http://localhost:8080`. To point it at another database, set `DB_URL`, `DB_USER` and `DB_PASSWORD`.

## Endpoints

| Method | Path | What it does |
|---|---|---|
| `POST` | `/api/expenses` | Creates an expense and returns `201` with a `Location` header |
| `GET` | `/api/expenses/{id}` | Returns one expense, or `404` if it doesn't exist |
| `GET` | `/api/expenses?category=FOOD` | Lists expenses, newest first, optionally filtered by category |

The valid categories are `FOOD`, `TRANSPORT`, `HOUSING`, `HEALTH`, `EDUCATION`, `LEISURE` and `OTHER`.

Creating an expense:

```bash
curl -i -X POST http://localhost:8080/api/expenses \
  -H "Content-Type: application/json" \
  -d '{"description":"Supermercado","amount":150.75,"category":"FOOD","date":"2026-10-15"}'
```

If the body is invalid, the API answers `400` and lists the fields that failed:

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

`ExpenseControllerTest` uses `@WebMvcTest` to cover the `POST` endpoint, the validation errors and the `404`. `ExpenseRepositoryTest` uses `@DataJpaTest` against an in-memory H2 database in PostgreSQL mode, so the suite runs without Docker. `ExpenseServiceTest` covers the service with Mockito.

## What's next

- Totals by category and by month
- `PUT` and `DELETE`, plus pagination
- Flyway migrations instead of `ddl-auto: update`
- Repository tests against a real PostgreSQL with Testcontainers
- OpenAPI documentation
