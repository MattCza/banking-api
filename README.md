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
- JaCoCo
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
- Unit tests uses a JaCoCo line-coverage gate on `app-backend`
- Every CI run publishes an [Allure report](https://mattcza.github.io/banking-api/) combining smoke and regression results

## Project Structure

```text
Money
|-- app-backend
|-- testing-framework
|-- docs
|-- .github/workflows
```

## Running the Project

There are two kinds of tests, and they need different things:

| Tests                                            | Command                    | Needs a running backend? | Needs Docker? |
|--------------------------------------------------|----------------------------|--------------------------|---------------|
| Unit tests (`app-backend`, Mockito, JaCoCo gate) | `mvn -pl app-backend test` | No                       | No            |
| API tests (`testing-framework`, REST Assured)    | Three options below        | **Yes**                  | Optional      |

The API tests call a backend over HTTP. **Maven does not start the backend for them.**  
The three options below only differ in *where the backend runs*.

`-Denv` only chooses the URL the tests call. It does not choose whether Docker is used or not.

| `-Denv=`          | Tests call                | Use when                                            |
|-------------------|---------------------------|-----------------------------------------------------|
| `local` (default) | `http://localhost:8080`   | tests run on your machine / CI agent                |
| `docker`          | `http://app-backend:8080` | tests run inside the Docker Compose (`test-runner`) |

### Option A - Everything in Docker Compose (PostgreSQL)

```bash
copy .env.example .env
docker compose up --build
```

Starts PostgreSQL, the backend, and a `test-runner` container that waits for the backend and then runs the whole
suite with `-Denv=docker`. The test summary is in the runner's logs - exit code tells if it passed:

```bash
docker compose logs test-runner
docker compose ps -a
```

Use `--build` after changing any code or Dockerfile, otherwise Docker may reuse an old image.

### Option B - backend in Docker, tests from your machine (PostgreSQL)

This is what Jenkins does.

```bash
copy .env.example .env
docker compose up -d --build postgres-db app-backend
mvn -pl testing-framework -am verify -Denv=local
```

### Option C - without Docker (H2)

Start the backend with the `test` profile, which uses an in-memory H2 database (PostgreSQL compatibility mode), then
run the tests from a second terminal:

```bash
mvn -pl app-backend -am package -DskipTests
java -jar app-backend/target/app-backend-1.0-SNAPSHOT.jar --spring.profiles.active=test
```

```bash
mvn -pl testing-framework -am verify -Denv=local
```

Fastest way to iterate. The data is lost on every restart. H2 only emulates PostgreSQL, so real PostgreSQL behavior is only proven by options A and B.

### Stopping the environment

```bash
docker compose down -v --remove-orphans
```

### Running the backend from IntelliJ

Run `BankingApplication` with a Spring profile, otherwise it will not start - the base `application.properties` has no
datasource. Add `-Dspring.profiles.active=<profile>` to the VM options:

- `test` - in-memory H2, nothing else needed
- `local` - PostgreSQL on `localhost:5432` (for example `docker compose up -d postgres-db`); set `DB_PASSWORD` from
  `.env.example` in the run configuration's environment variables

## Spring Profiles and Environment Files

Backend profiles (`app-backend/src/main/resources`):

| File                            | Profile  | Database                         | Used by                                                          |
|---------------------------------|----------|----------------------------------|------------------------------------------------------------------|
| `application.properties`        | always   | none                             | shared settings (port, JWT); not runnable on its own             |
| `application-local.properties`  | `local`  | PostgreSQL on `localhost:5432`   | IntelliJ, GitHub Actions                                         |
| `application-docker.properties` | `docker` | PostgreSQL on host `postgres-db` | the `app-backend` container in Docker Compose                    |
| `application-test.properties`   | `test`   | in-memory H2                     | option C, and the boot check run by `mvn -pl app-backend verify` |

Test framework environments (`testing-framework/src/main/resources`): `env-local.properties` and `env-docker.properties`
hold the base URL, port, and path described in the `-Denv` table above.

## Where Things Run

| Where                  | Backend                                         | Database           | Tests                                   |
|------------------------|-------------------------------------------------|--------------------|-----------------------------------------|
| `docker compose up`    | container, profile `docker`                     | PostgreSQL         | `test-runner` container, `-Denv=docker` |
| Jenkins                | container from Docker Compose, profile `docker` | PostgreSQL         | Maven on the agent, `-Denv=local`       |
| GitHub Actions         | `java -jar` on the runner, profile `local`      | PostgreSQL service | Maven on the runner, `-Denv=local`      |
| Your machine, option C | `java -jar` or IntelliJ, profile `test`         | H2                 | Maven, `-Denv=local`                    |

## Local Authentication

The project seeds demo users for development and test scenarios:

- `admin / admin123` - role `ADMIN`, can create, update, and delete accounts
- `user / user123` - role `USER`, read-only access to accounts

These credentials are intended for local and CI usage only.

## Notes

- The API tests are `*IT` classes run by Failsafe, so they need `verify` (not `test`) - `mvn test` skips them by design.
- Jenkins starts Docker services first and then runs the Failsafe suite.
- `.env.example` contains the minimum variables needed for the Docker Compose setup.
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
