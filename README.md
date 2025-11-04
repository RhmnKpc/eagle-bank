# Eagle Bank — REST API

A Spring Boot 3 (Java 21) REST API that models a simple retail banking system with users, accounts, and transactions. The codebase follows a clean/hexagonal architecture style separating domain, application (use cases), infrastructure, and interface (REST) layers.

## Table of Contents
- [Features](#features)
- [Architecture Overview](#architecture-overview)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Build & Run](#build--run)
- [Configuration](#configuration)
- [API Overview](#api-overview)
- [Security](#security)
- [Persistence](#persistence)
- [Testing](#testing)
- [OpenAPI / Swagger UI](#openapi--swagger-ui)
- [Conventions](#conventions)

## Features
- User management (create, update, fetch)
- Bank accounts (create, update, list) with account number, sort code, type, status
- Transactions (deposit, withdraw, list)
- Validation, error handling, and domain-specific exceptions
- JWT-based authentication and authorization
- OpenAPI docs and Swagger UI

## Architecture Overview
The project is organized around a clean architecture/DDD approach:

- Domain layer (`com.eaglebank.domain`)
  - Core entities/value objects: `User`, `Account`, `Transaction`, etc.
  - Domain services: `AccountDomainService`, `TransactionDomainService`
  - Domain exceptions: e.g., `AccountNotFoundException`, `InsufficientFundsException`
  - Pure business rules; no framework or I/O concerns.

- Application layer (`com.eaglebank.application`)
  - Use case services orchestrating domain logic, e.g., `AccountServiceImpl`, `TransactionServiceImpl`, `UserServiceImpl`
  - Ports (interfaces) like `AccountService`, `UserService`

- Infrastructure layer (`com.eaglebank.infrastructure`)
  - Persistence adapters (`...persistence.adapter`) and Spring Data JPA repositories
  - Security components (JWT filter, token provider, password encoder)
  - Mappers between domain and persistence models

- Interfaces layer (`com.eaglebank.interfaces.rest`)
  - REST controllers for Accounts, Users, Transactions
  - DTOs for requests/responses
  - REST mappers, global exception handler

- Configuration (`com.eaglebank.config`)
  - Spring configuration (JPA, Security, OpenAPI, Bean wiring)

## Tech Stack
- Java 21, Spring Boot 3
- Spring Web, Spring Data JPA, Spring Security, Validation, Actuator
- JWT (jjwt)
- Databases: H2 (dev), PostgreSQL (prod/test)
- OpenAPI via `springdoc-openapi`
- Gradle build
- Testing: JUnit 5, Spring Boot Test, Testcontainers, RestAssured

## Project Structure
```
src/
  main/java/com/eaglebank/
    application/            # Use case services
    config/                 # Spring configs (JPA, Security, OpenAPI)
    domain/                 # Entities, VOs, domain services, exceptions
    infrastructure/         # Adapters (persistence, security) and mappers
    interfaces/rest/        # Controllers, DTOs, mappers, exception handler
  resources/
    application.yml         # Base config + profiles (dev, prod)
    application-dev.yml
    application-prod.yml
```
See the repository root for Gradle build files (`build.gradle`, `settings.gradle`).

## Build & Run
Prerequisites:
- JDK 21
- Docker (optional; used by Testcontainers during tests)

Build the project:
```
./gradlew clean build
```

Run with the default profile (dev):
```
./gradlew bootRun --args='--spring.profiles.active=dev'
```

Alternatively, run the fat jar after building:
```
java -jar build/libs/eagle-bank-1.0.0.jar --spring.profiles.active=dev
```

## Configuration
Configuration is managed via Spring profiles and YAML files:
- `src/main/resources/application.yml` (base)
- `src/main/resources/application-dev.yml`
- `src/main/resources/application-prod.yml`

Common environment variables (examples):
- `DATABASE_URL` (e.g., `jdbc:postgresql://localhost:5432/eaglebank`)
- `DATABASE_USERNAME`, `DATABASE_PASSWORD`
- `JWT_SECRET` (used by the token provider)
- `PORT` (optional, overrides default)

Note: H2 is included as a runtime dependency for development convenience. PostgreSQL is the target DB for production.

## API Overview
Controllers are under `com.eaglebank.interfaces.rest.controller`.
Typical endpoints (paths are indicative; refer to Swagger UI for the latest):

- Auth:
  - `POST /api/v1/auth/login` — Obtain JWT

- Users:
  - `POST /api/v1/users` — Create a user
  - `GET /api/v1/users/{id}` — Get user by ID
  - `PATCH /api/v1/users/{id}` — Update user
  - `DELETE /api/v1/users/{id}` — Delete user

- Accounts:
  - `POST /api/v1/accounts` — Create bank account for a user
  - `PATCH /api/v1/accounts/{accountId}` — Update account details (e.g., status)
  - `GET /api/v1/accounts` — List authenticated user’s accounts
  - `GET /api/v1/accounts/{accountId}` — Get a specific account
  - `DELETE /api/v1/accounts/{accountId}` — Delete account

- Transactions:
  - `POST /api/v1/transactions` — Create a transaction (deposit/withdraw/transfer)
  - `GET /api/v1/transactions` — List transactions, with optional filters
  - `GET /api/v1/transactions/{id}` — Get a specific transaction

### Sample cURL
Authenticate and call a protected endpoint:
```
# Login to get a JWT
override_base_url=http://localhost:8080
curl -s \
  -X POST "$override_base_url/api/v1/auth/login" \
  -H 'Content-Type: application/json' \
  -d '{"email":"john.doe@example.com","password":"Passw0rd!"}' | jq -r '.token'

# Use the token to list accounts
TOKEN="<paste-token-here>"
curl -H "Authorization: Bearer $TOKEN" "$override_base_url/api/v1/accounts"
```

## Security
- JWT-based stateless authentication with a custom filter (`JwtAuthenticationFilter`) and token provider (`JwtTokenProvider`).
- Unauthorized and access denied responses are returned as JSON via dedicated handlers.
- Passwords are hashed via a Spring `PasswordEncoder` adapter.

## Persistence
- Domain models are mapped to JPA entities in `infrastructure.persistence.entity`.
- Adapters convert between domain and persistence models using mappers.
- Spring Data repositories reside under `infrastructure.persistence.repository` and are wrapped by adapter classes.

## Testing
- Unit tests for domain objects (value objects and aggregates)
- Application service tests
- Integration tests for REST controllers using Spring Boot Test + RestAssured
- Testcontainers for PostgreSQL integration in tests

Run all tests:
```
./gradlew test
```

## OpenAPI / Swagger UI
Swagger UI is enabled via `springdoc-openapi`. Once the app is running, visit:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Conventions
- Package-by-layer structure with ports-and-adapters mapping
- DTOs strictly for API boundaries; domain remains framework-agnostic
- Exceptions surface consistent error responses via `GlobalExceptionHandler`

