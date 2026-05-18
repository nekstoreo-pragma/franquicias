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

## Prerequisites

- Java 25
- Gradle 9.4+

## Getting Started

```bash
./gradlew bootRun
```

### Build

```bash
./gradlew build
```

### Run tests

```bash
./gradlew test
./gradlew jacocoMergedReport   # coverage report → build/reports/
./gradlew pitest               # mutation testing
```
