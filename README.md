# Money API Testing Framework

Money is a portfolio project that combines a Spring Boot banking API with a dedicated REST Assured test framework in one repository.

The goal of the project is to show practical backend and QA engineering skills together:

- building a secured REST API
- designing reusable API test architecture
- running repeatable environments with Docker
- documenting strategy and coverage like in a real team

## Architecture

The repository is split into two modules:

- `app-backend` - Spring Boot REST API with JWT authentication, Flyway migrations, and PostgreSQL support
- `testing-framework` - REST Assured integration test suite with reusable clients, DTOs, builders, and assertions

Supporting project files:

- `docs/TestStrategy.md`
- `docs/TestCoverage.md`
- `docker-compose.yml`
- `Jenkinsfile`

## Stack

- Java 21
- Spring Boot 3
- Spring Security
- REST Assured
- JUnit 5
- AssertJ
- PostgreSQL
- H2
- Flyway
- Docker Compose
- Jenkins
- Maven

## Key Features

- JWT-secured authentication flow
- Account create, read, update, and delete endpoints
- Validation and error contract handling
- Duplicate email protection
- Parallel request coverage for concurrency behavior
- Multi-module Maven structure for backend and test automation

## Project Structure

```text
Money
|-- app-backend
|-- testing-framework
`-- docs
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

- `admin / admin123`
- `user / user123`

These credentials are intended for local and CI usage only.

## Notes

- The integration tests expect the backend to be running before `testing-framework` starts.
- Jenkins starts Docker services first and then runs the Failsafe suite.
- `.env.example` contains the minimum variables needed for local setup.
- The default local JWT secret is for development only and should be overridden in real deployments.

## Coverage Highlights

- Authentication happy-path and negative scenarios
- Account create, get, delete, and update flows
- Validation and error contract checks
- JWT security coverage for protected endpoints
- Duplicate email concurrency coverage

## Next Improvements

- add more unit test coverage around service and security logic
- expand PUT endpoint validation and authorization coverage
- move CI visibility to GitHub Actions in addition to Jenkins
- add transfer and transaction scenarios
