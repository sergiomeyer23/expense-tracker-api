# expense-tracker-api

![CI](https://github.com/sergiomeyer23/expense-tracker-api/actions/workflows/ci.yml/badge.svg)

A REST API for tracking personal expenses, built with Java 21, Spring Boot, JPA and PostgreSQL. I built it as a portfolio project to practice a full backend slice: layered code, validated DTOs, global error handling, a database in Docker Compose and tests.

API REST para controle de despesas pessoais, feita com Java 21, Spring Boot, JPA e PostgreSQL.

## Stack

- Java 21, Spring Boot 3.5, Maven (the wrapper is included)
- Spring Web, Spring Data JPA, Bean Validation
- Flyway for schema migrations, springdoc OpenAPI (Swagger UI) and Spring Boot Actuator
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

The API listens on `http://localhost:8080`. Flyway creates the schema on startup. To point it at another database, set `DB_URL`, `DB_USER` and `DB_PASSWORD`.

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

## API docs, health and schema

| URL | What it is |
|---|---|
| `/swagger-ui.html` | Interactive Swagger UI |
| `/v3/api-docs` | OpenAPI 3 document as JSON |
| `/actuator/health` | Health check (`UP` when the app and the database respond) |
| `/actuator/info` | Basic application info |
| `/actuator/metrics` | Micrometer metrics |

The schema lives in `src/main/resources/db/migration`. `V1__create_expenses_table.sql` creates the `expenses` table, a check constraint for the categories and an index on `(category, date DESC)` for the category filter. New schema changes go in a new `V2__...` file.

## Tests

```bash
./mvnw test
```

`ExpenseControllerTest` uses `@WebMvcTest` to cover the `POST` endpoint, the validation errors and the `404`. `ExpenseRepositoryTest` uses `@DataJpaTest` against an in-memory H2 database in PostgreSQL mode, so the suite runs without Docker. `ExpenseServiceTest` covers the service with Mockito. `ApiIntegrationTest` starts the whole application on H2 with the Flyway migration applied and Hibernate in `validate` mode, then checks the happy path, `400`, `404`, `/actuator/health` and `/v3/api-docs`.

The tests run on H2, so the migration has not been exercised against a real PostgreSQL by the suite. Testcontainers is on the list below.

GitHub Actions runs `./mvnw -B clean test` on every push and pull request to `main` (`.github/workflows/ci.yml`).

## Architecture and trade-offs

- **Java 21 and Spring Boot 3.5.** Records keep the DTOs short, and the current LTS avoids a near-term upgrade.
- **Flyway with `ddl-auto: validate`.** Hibernate no longer changes the schema. Migrations are versioned SQL files, and at startup Hibernate checks that the entities still match the tables, so a drift fails fast. The cost is writing SQL by hand for each change.
- **DTOs with Bean Validation.** `ExpenseRequest` holds the rules (non-blank description, positive amount with at most 2 decimals, date not in the future). The entity never reaches the HTTP layer, so the API contract can change without touching the table.
- **One `@RestControllerAdvice`.** `GlobalExceptionHandler` turns validation errors, unknown ids, malformed JSON and unexpected failures into RFC 7807 `ProblemDetail` responses, so every error has the same shape and stack traces stay out of the response.
- **H2 for tests.** The suite runs without Docker and finishes in seconds. It does not catch PostgreSQL-specific behavior.

## What's next

- Totals by category and by month
- `PUT` and `DELETE`, plus pagination
- Repository tests against a real PostgreSQL with Testcontainers
