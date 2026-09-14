# API Test Strategy

## 1. Purpose

This document describes the testing strategy for the project.

The system under test is a Spring Boot banking API secured with JWT. 
The automated test layer is implemented as a separate REST Assured framework 
to verify business behavior, validation, security, and API contracts through the 
public interface.

The main goals of the strategy are to:

- verify core business flows
- detect regressions quickly
- validate security and error handling
- keep test execution repeatable across local and CI environments


## 2. Scope

### In Scope

- authentication endpoint behavior
- account create, read, update, and delete flows
- deposit and withdrawal transaction flows
- multi-currency account support and currency-mismatch handling
- request validation rules
- HTTP status code correctness
- API error response contract
- API response contract validation via JSON Schema
- authorization checks for protected endpoints
- duplicate email and concurrency behavior


## 3. Test Levels

| Test Level            | Current Status                         |
|-----------------------|----------------------------------------|
| Unit Tests            | Implemented for selected service logic |
| API Integration Tests | Implemented                            |
| End-to-End Tests      | Planned                                |
| Performance Tests     | Planned                                |

The primary focus of the project is API integration testing.  
Unit tests are used to protect core service logic and edge cases at lower cost, 
following the Shift-Left approach by identifying defects early in the SDLC.


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
- role-based access control (only ADMIN may create accounts)
- response contract validation

`GET /api/v1/accounts/{id}`

Covered areas:

- fetch existing account
- account not found
- invalid path parameter
- read access permitted for any authenticated role

`PUT /api/v1/accounts/{id}`

Covered areas:

- successful update of account data
- update with unchanged email
- conflict when email belongs to another account
- duplicate email under parallel requests
- validation of owner name, email, and balance
- role-based access control (only ADMIN may update accounts)

`DELETE /api/v1/accounts/{id}`

Covered areas:

- delete existing account
- verify resource is not available after deletion
- account not found
- invalid path parameter
- role-based access control (only ADMIN may delete accounts)

### Transactions

`POST /api/v1/accounts/{id}/deposit`  
`POST /api/v1/accounts/{id}/withdraw`

Covered areas:

- successful deposit and withdrawal, verified against the account's resulting balance
- validation of amount (null, zero, negative, precision) and currency (null)
- currency-mismatch between the request and the account's currency
- insufficient funds on withdrawal
- account not found
- role-based access control (only ADMIN may deposit or withdraw)
- concurrency: parallel withdrawals against the same account never push the balance negative


## 6a. Test Categorization

Test classes are tagged with JUnit 5 `@Tag` with:

- **category** – `functional`, `validation`, `security`, or `concurrency`, describing type of risk covered by the tests.
- **execution tier** – `smoke` - one happy-path test per resource (login, create/get/update/delete account), identifying it as part of a small, fast subset

This allows the same test suite be sliced in two independent ways:  
by content (`-Dgroups=security`) or by how urgently it needs to run (`-Dgroups=smoke`). No test duplication or package restructuring is required.


## 7. Test Data Strategy

The framework relies primarily on dynamically generated test data. Each test run creates new, unique data at runtime, allowing the same test suite to be executed consistently across different environments without relying on pre-existing data or configuration.


### Static Data

Static data is used for application configuration rather than test scenarios:
- seeded local users for authentication
- environment-specific base URLs and ports


### Dynamic Data

Generated data is used for:

- owner names
- email addresses
- monetary values and currencies

Dynamic test data reduces collisions between test runs and improves repeatability across environments.

## 8. Test Environments

| Environment | Purpose                                               |
|-------------|-------------------------------------------------------|
| Local       | developer execution against a locally running backend |
| Docker      | repeatable local integration setup                    |
| Jenkins     | continuous integration execution                      |

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

The project includes Jenkins-based CI for running the API test suite, split into two sequential stages:

1. **Smoke Tests** (`-Dgroups=smoke`) – a small, fast subset covering one happy path per resource. If this stage fails, the pipeline stops immediately.
2. **Regression Tests** (`-DexcludedGroups=smoke`) – the full validation, security, and concurrency suite, run only after smoke passes.

The repository also includes a GitHub Actions workflow for fast verification of the build and unit tests on GitHub.

The CI objective is to provide fast feedback after code changes, fail quickly on broken core functionality, and prevent obvious regressions before merge or publication.

## 11. Risks and Future Work

Current gaps that are intentionally left for future iterations:

- transfer between accounts
- performance and resilience testing
- richer reporting and quality gates
- Testcontainers-based environment management

## 12. Summary

The Money API test strategy is designed to demonstrate practical QA and backend engineering skills in a portfolio-ready project.

It prioritizes the highest-risk API behaviors, keeps the test suite maintainable through reusable framework layers, and supports repeatable execution in local and CI environments.
