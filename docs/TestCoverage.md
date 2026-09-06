# Test Coverage

This document lists the API scenarios currently covered by
automation in the repository.

## Test Coverage Approach

For each endpoint, the test suite aims to cover the following areas:

- **Happy Path** – valid request and expected successful response
- **Validation** – null, empty/blank, invalid format, and boundary values
- **Business Rules** – business-specific constraints such as duplicate email handling
- **Security** – missing, invalid, and expired JWT; role-based access control
- **Error Handling** – correct HTTP status code and error response structure
- **Negative Scenarios** – invalid input, non-existing resources, and malformed requests

## Authentication

### POST /api/v1/auth/login

| Scenario             | Priority | Automated | Status |
|----------------------|----------|-----------|--------|
| Valid login          | Critical | Yes       | Done   |
| Invalid username     | Critical | Yes       | Done   |
| Invalid password     | Critical | Yes       | Done   |
| Blank username       | High     | Yes       | Done   |
| Blank password       | High     | Yes       | Done   |
| Username null        | High     | Yes       | Done   |
| Password null        | High     | Yes       | Done   |
| Empty request body   | High     | Yes       | Done   |
| Malformed JSON       | Medium   | Yes       | Done   |
| Missing Content-Type | Medium   | Yes       | Done   |
| Wrong Content-Type   | Medium   | Yes       | Done   |

## Accounts

### POST /api/v1/accounts

| Scenario                                | Priority | Automated | Status |
|-----------------------------------------|----------|-----------|--------|
| Valid account                           | Critical | Yes       | Done   |
| Duplicate email                         | Critical | Yes       | Done   |
| Duplicate email under parallel requests | Critical | Yes       | Done   |
| Empty owner name                        | High     | Yes       | Done   |
| Null owner name                         | High     | Yes       | Done   |
| Too short owner name                    | High     | Yes       | Done   |
| Too long owner name                     | High     | Yes       | Done   |
| Empty email                             | High     | Yes       | Done   |
| Null email                              | High     | Yes       | Done   |
| Too long email                          | High     | Yes       | Done   |
| Null initial balance                    | High     | Yes       | Done   |
| Negative initial balance                | High     | Yes       | Done   |
| Too many integer digits in balance      | High     | Yes       | Done   |
| Too many decimal places in balance      | High     | Yes       | Done   |
| Missing JWT                             | Critical | Yes       | Done   |
| Invalid JWT                             | Critical | Yes       | Done   |
| Expired JWT                             | Critical | Yes       | Done   |
| Regular user attempts to create account | Critical | Yes       | Done   |

### GET /api/v1/accounts/{id}

| Scenario                      | Priority | Automated | Status |
|-------------------------------|----------|-----------|--------|
| Existing account              | Critical | Yes       | Done   |
| Non-existing account          | High     | Yes       | Done   |
| Non-numeric ID                | High     | Yes       | Done   |
| Missing JWT                   | Critical | Yes       | Done   |
| Invalid JWT                   | Critical | Yes       | Done   |
| Expired JWT                   | Critical | Yes       | Done   |
| Regular user can view account | Medium   | Yes       | Done   |

### PUT /api/v1/accounts/{id}

| Scenario                                       | Priority | Automated | Status |
|------------------------------------------------|----------|-----------|--------|
| Update existing account                        | Critical | Yes       | Done   |
| Update with unchanged email                    | High     | Yes       | Done   |
| Update with email belonging to another account | High     | Yes       | Done   |
| Duplicate email under parallel requests        | Critical | Yes       | Done   |
| Empty owner name                               | High     | Yes       | Done   |
| Null owner name                                | High     | Yes       | Done   |
| Too short owner name                           | High     | Yes       | Done   |
| Too long owner name                            | High     | Yes       | Done   |
| Empty email                                    | High     | Yes       | Done   |
| Null email                                     | High     | Yes       | Done   |
| Too long email                                 | High     | Yes       | Done   |
| Null balance                                   | High     | Yes       | Done   |
| Negative balance                               | High     | Yes       | Done   |
| Too many integer digits in balance             | High     | Yes       | Done   |
| Too many decimal places in balance             | High     | Yes       | Done   |
| Missing JWT                                    | Critical | Yes       | Done   |
| Invalid JWT                                    | Critical | Yes       | Done   |
| Expired JWT                                    | Critical | Yes       | Done   |
| Regular user attempts to update account        | Critical | Yes       | Done   |

### DELETE /api/v1/accounts/{id}

| Scenario                                      | Priority | Automated | Status |
|-----------------------------------------------|----------|-----------|--------|
| Delete existing account                       | Critical | Yes       | Done   |
| Verify deleted account is no longer available | Critical | Yes       | Done   |
| Delete non-existing account                   | High     | Yes       | Done   |
| Delete non-numeric ID                         | Medium   | Yes       | Done   |
| Missing JWT                                   | Critical | Yes       | Done   |
| Invalid JWT                                   | Critical | Yes       | Done   |
| Expired JWT                                   | Critical | Yes       | Done   |
| Regular user attempts to delete account       | Critical | Yes       | Done   |

## Test Categorization

Every test class is tagged with JUnit 5 `@Tag` to classify what kind of risk it covers:

| Tag           | Meaning                                               |
|---------------|-------------------------------------------------------|
| `functional`  | happy-path and core business-rule behavior            |
| `validation`  | field-level validation (null, blank, boundary values) |
| `security`    | missing, invalid, and expired JWT handling            |
| `concurrency` | race-condition and parallel-request behavior          |

Independently of category, one happy-path test per resource (login, create/get/update/delete account) is also tagged
`smoke` — a fast, curated subset used to gate CI before the full regression suite runs (`-Dgroups=smoke` /
`-DexcludedGroups=smoke`).

## Current Gaps

The following areas are not yet covered and are good candidates for future work:

- transfer and transaction flows
- performance and resilience testing
- API response schema validation
- concurrency coverage for future transfer/transaction endpoints
- test reporting and execution history beyond Jenkins' built-in test result view