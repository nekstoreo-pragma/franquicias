# Franchise Management API

REST API for managing a franchise network, built with Spring WebFlux and Clean Architecture.

## Architecture

This project follows **Clean Architecture** using the [Bancolombia scaffold](https://github.com/bancolombia/scaffold-clean-architecture), organized in concentric layers where the domain has no external dependencies.

```
domain/
  model/       → Entities and gateway interfaces (ports)
  usecase/     → Business logic, orchestrates domain operations

infrastructure/
  entry-points/
    reactive-web/     → RouterFunction + Handler (WebFlux, no @RestController)
  driven-adapters/
    dynamo-db/        → DynamoDB implementation of domain gateways
  helpers/
    metrics/          → Micrometer / CloudWatch integration

applications/
  app-service/  → Spring Boot wiring and entry point
```

## Tech Stack

| Layer | Technology |
|---|---|
| Runtime | Java 25 |
| Framework | Spring Boot 4.0.5 + Spring WebFlux |
| Persistence | AWS DynamoDB SDK 2.42.23 |
| API Docs | SpringDoc OpenAPI 3.0.2 |
| Build | Gradle 9.4.1 |

**Key design decisions:**
- **Reactive end-to-end** — Spring WebFlux + DynamoDB Async Client. No blocking I/O at any layer.
- **One table per entity** — `franchises`, `branches`, `products`, each with a GSI for relational queries.
- **Fail-fast validation** — invalid inputs are rejected at the handler boundary before reaching the use case.

## API Reference

### Franchises

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/v1/franchises` | Create a franchise |
| `PATCH` | `/api/v1/franchises/{id}/name` | Update franchise name |
| `GET` | `/api/v1/franchises/{id}/top-products` | Top-stock product per branch |

### Branches

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/v1/franchises/{franchiseId}/branches` | Add a branch to a franchise |
| `PATCH` | `/api/v1/branches/{id}/name` | Update branch name |

### Products

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/v1/branches/{branchId}/products` | Add a product to a branch |
| `DELETE` | `/api/v1/products/{id}` | Remove a product |
| `PATCH` | `/api/v1/products/{id}/stock` | Update stock |
| `PATCH` | `/api/v1/products/{id}/name` | Update product name |

### Examples

**Create a franchise**

```bash
curl -X POST http://localhost:8080/api/v1/franchises \
  -H "Content-Type: application/json" \
  -d '{"name": "Burger King"}'
```

```json
{ "id": "3f2504e0-4f89-11d3-9a0c-0305e82c3301", "name": "Burger King" }
```

**Get top-stock product per branch**

```bash
curl http://localhost:8080/api/v1/franchises/{franchiseId}/top-products
```

```json
[
  {
    "branch": { "id": "...", "name": "Sede Norte", "franchiseId": "..." },
    "topProduct": { "id": "...", "name": "Whopper", "stock": 99, "branchId": "..." }
  }
]
```

Full request/response schemas are available in the Swagger UI at `http://localhost:8080/v3/swagger-ui.html`.

## Prerequisites

- Java 25
- Docker
- AWS CLI v2

## Getting Started

**1. Start DynamoDB Local**

```bash
docker run -d --name dynamodb-local -p 8000:8000 \
  amazon/dynamodb-local -jar DynamoDBLocal.jar -sharedDb -inMemory
```

**2. Create tables**

```bash
./scripts/create-tables-local.sh
```

**3. Start the application**

```bash
./gradlew bootRun
```

The API is available at `http://localhost:8080`.

### Run with Docker

```bash
# Build the image
docker build -t franchise-api .

# Run (local profile connects to DynamoDB Local)
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=local \
  -e AWS_DYNAMODB_ENDPOINT=http://host.docker.internal:8000 \
  franchise-api
```

### Build

```bash
./gradlew build
```

### Run tests

```bash
./gradlew test
./gradlew jacocoMergedReport   # coverage report → build/reports/jacocoHtml/
./gradlew pitest               # mutation testing
```
