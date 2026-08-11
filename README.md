# Money API Testing Framework

Money is a sample banking project built to practice backend quality and API automation in one repository.

The repo contains:

- `app-backend`: Spring Boot REST API with JWT authentication and Flyway migrations
- `testing-framework`: REST Assured integration test suite
- `docs`: test strategy, coverage, and architectural decisions

## Stack

- Java 21
- Spring Boot
- REST Assured
- JUnit 5
- PostgreSQL
- H2
- Flyway
- Docker Compose
- Jenkins
- Maven

## Project Structure

```text
Money
|-- app-backend
|-- testing-framework
`-- docs
```

## Running Locally

1. Start the application dependencies:

```bash
copy .env.example .env
docker compose up -d postgres-db app-backend
```

2. Run the API tests against the running backend:

```bash
mvn -pl testing-framework -am verify -Denv=local
```

3. Stop the environment when you are done:

```bash
docker compose down -v --remove-orphans
```

## Notes

- The integration tests expect the backend to be running before `testing-framework` starts.
- Jenkins starts Docker services first and then runs the Failsafe suite.
- `.env.example` contains the minimum variables needed for local setup.

## Documentation

- `docs/TestStrategy.md`
- `docs/TestCoverage.md`
- `docs/Decisions.md`

## Current Coverage Highlights

- Authentication happy-path and negative scenarios
- Account create, get, and delete flows
- Validation and error contract checks
- JWT security coverage for POST, GET, and DELETE endpoints
- Duplicate email concurrency coverage
