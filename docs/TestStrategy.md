# API Test Strategy

## 1. Purpose

This document describes the testing strategy for the Money API project.

The system under test is a Spring Boot banking API secured with JWT. The automated test layer is implemented as a separate REST Assured framework to verify business behaviour, validation, security, and API contracts through the public interface.

The main goals of the strategy are to:

- verify core business flows
- detect regressions quickly
- validate security and error handling
- keep test execution repeatable across local and CI environments

## 2. Scope

### In Scope

- authentication endpoint behavior
- account create, read, update, and delete flows
- request validation rules
- HTTP status code correctness
- API error response contract
- authorization checks for protected endpoints
- duplicate email and concurrency behavior

## 3. Test Levels

| Test Level | Current Status |
|------------|----------------|
| Unit Tests | Implemented for selected service logic |
| API Integration Tests | Implemented |
| End-to-End Tests | Planned |
| Performance Tests | Planned |

The primary focus of the project is API integration testing. Unit tests are used to protect core service logic and edge cases at lower cost.

## 4. Test Types

The suite combines:

- functional testing
- validation testing
- negative testing
- security testing
- regression testing
- API contract testing
- concurrency testing for high-risk scenarios

## 5. Test Design Approach

The test suite follows a risk-based approach. Highest-priority scenarios are covered first, especially around authentication, authorization, account management, and validation.

The following design techniques are used:

- boundary value analysis for field lengths and numeric precision
- equivalence partitioning for valid and invalid request data
- negative testing for malformed or incomplete input
- business-rule verification for duplicate data and not-found conditions

Tests are written using a consistent Arrange-Act-Assert structure and named with business intent in mind.

## 6. Functional Areas Covered

### Authentication

`POST /api/v1/auth/login`

Covered areas:

- valid login
- invalid credentials
- blank and null fields
- empty request body
- malformed JSON
- missing or wrong content type
- JWT presence in successful response

### Accounts

`POST /api/v1/accounts`

Covered areas:

- successful account creation
- validation of owner name, email, and balance
- duplicate email handling
- duplicate email under parallel requests
- protected endpoint authorization checks
- response contract validation

`GET /api/v1/accounts/{id}`

Covered areas:

- fetch existing account
- account not found
- invalid path parameter
- protected endpoint authorization checks

`PUT /api/v1/accounts/{id}`

Covered areas:

- successful update of account data
- update with unchanged email
- conflict when email belongs to another account

`DELETE /api/v1/accounts/{id}`

Covered areas:

- delete existing account
- verify resource is not available after deletion
- account not found
- invalid path parameter
- protected endpoint authorization checks

## 7. Test Data Strategy

The framework uses both static and dynamic test data.

### Static Data

- seeded local users for authentication
- environment-specific base URLs and ports

### Dynamic Data

Generated data is used for:

- owner names
- email addresses
- monetary values

Dynamic test data reduces collisions between test runs and improves repeatability across environments.

## 8. Test Environments

| Environment | Purpose |
|-------------|---------|
| Local | developer execution against a locally running backend |
| Docker | repeatable local integration setup |
| Jenkins | continuous integration execution |

Environment selection is controlled through Maven properties:

```bash
mvn -pl testing-framework -am verify -Denv=local
```

## 9. Entry and Exit Criteria

### Entry Criteria

- backend application is available
- database migrations completed successfully
- required environment variables are configured
- selected target environment is reachable

### Exit Criteria

- all automated checks pass
- no unexpected 5xx responses appear
- error contracts remain consistent
- protected endpoints enforce authentication as expected

## 10. CI Execution

The project includes Jenkins-based CI for running the API regression suite. The repository also includes a GitHub Actions workflow for fast verification of the build and unit tests on GitHub.

The CI objective is to provide fast feedback after code changes and prevent obvious regressions before merge or publication.

## 11. Risks and Future Work

Current gaps that are intentionally left for future iterations:

- broader PUT validation and security coverage
- transfer and transaction domain scenarios
- performance and resilience testing
- richer reporting and quality gates
- Testcontainers-based environment management

## 12. Summary

The Money API test strategy is designed to demonstrate practical QA and backend engineering skills in a portfolio-ready project.

It prioritizes the highest-risk API behaviors, keeps the test suite maintainable through reusable framework layers, and supports repeatable execution in local and CI environments.
