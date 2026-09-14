# Money API Testing Framework

Money is a portfolio project that combines a Spring Boot banking API with a dedicated REST Assured test framework in one repository.

The goal of the project is to show practical backend and QA engineering skills together:

- building a secured REST API
- designing reusable API test architecture
- running repeatable environments with Docker
- documenting strategy and coverage like in a real team

## Architecture

The repository is split into two modules:

- `app-backend` - Spring Boot REST API with JWT authentication, role-based authorization, Flyway migrations, and PostgreSQL support
- `testing-framework` - REST Assured integration test suite with reusable clients, DTOs, builders, and assertions

Supporting project files:

- `docs/TestStrategy.md`
- `docs/TestCoverage.md`
- `docker-compose.yml`
- `Jenkinsfile`
- `.github/workflows/ci.yml`

## Stack

- Java 21
- Spring Boot 3
- Spring Security (JWT + role-based method security)
- REST Assured
- JUnit 5
- AssertJ
- PostgreSQL
- H2
- Flyway
- Docker Compose
- Jenkins
- GitHub Actions
- Jacoco
- Allure Report
- Maven

## Key Features

- JWT-secured authentication flow
- Role-based authorization (`ADMIN` vs `USER`) on account mutation endpoints
- Account create, read, update, and delete endpoints
- Multi-currency accounts (`PLN`/`EUR`/`USD`/`GBP`) modeled as a `Money` value object (amount + currency)
- Deposit and withdrawal transactions, with currency-mismatch and insufficient-funds protection
- Concurrency-safe balance updates
- Validation and error contract handling
- Duplicate email protection, including under concurrent requests
- API response contract validation via JSON Schema
- Multi-module Maven structure for backend and test automation

## Quality & CI/CD

- Tests are tagged: **category** (`functional`, `validation`, `security`, `concurrency`) and **execution tier** (`smoke`)
- Both Jenkins and GitHub Actions run a two-stage pipeline: a fast `smoke` gate, and if it passes then the full regression suite
- Unit tests uses a Jacoco line-coverage gate on `app-backend`
- Every CI run publishes an [Allure report](https://mattcza.github.io/banking-api/) combining smoke and regression results

## Project Structure

```text
Money
|-- app-backend
|-- testing-framework
|-- docs
|-- .github/workflows
```

## Running Locally

1. Prepare local configuration:
```bash
copy .env.example .env
```
2. Start the database and backend with Docker:

```bash
docker compose up -d postgres-db app-backend
```

3. Run the API integration tests:

```bash
mvn -pl testing-framework -am verify -Denv=local
```

4. Stop the environment when you are done:

```bash
docker compose down -v --remove-orphans
```

## Local Authentication

The project seeds demo users for development and test scenarios:

- `admin / admin123` - role `ADMIN`, can create, update, and delete accounts
- `user / user123` - role `USER`, read-only access to accounts

These credentials are intended for local and CI usage only.

## Notes

- The integration tests expect the backend to be running before `testing-framework` starts.
- Jenkins starts Docker services first and then runs the Failsafe suite.
- `.env.example` contains the minimum variables needed for local setup.
- The default local JWT secret is for development only and should be overridden in real deployments.

## Coverage Highlights

- Authentication happy-path and negative scenarios
- Account create, get, delete, and update flows
- Deposit and withdrawal flows, including currency-mismatch and insufficient-funds conflicts
- Validation and error contract checks
- JSON Schema validation of response contracts
- JWT security coverage (missing/invalid/expired tokens) for every endpoint
- Role-based authorization coverage (403 for the wrong role, success for the right one)
- Duplicate email concurrency coverage on both create and update
- Concurrency coverage proving parallel withdrawals can't push a balance negative

## Next Improvements

- OpenAPI spec generation
- Testcontainers-based test data lifecycle, replacing the shared long-lived local database
- parallel test execution
- transfer between accounts
