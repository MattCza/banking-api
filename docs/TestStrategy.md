# API Test Strategy

## 1. Purpose

The purpose of this document is to describe the **testing strategy** for the **Money API** project.

The project simulates a banking REST API and includes an automated API testing framework built with Java and REST Assured.

The objective of the test strategy is to ensure that the API is:

- Functionally correct
- Secure
- Reliable
- Stable after future changes
- Ready for Continuous Integration

---

# 2. Scope

## In Scope

The following areas are covered by automated API tests:

- Authentication (JWT)
- Account creation
- Account retrieval
- Account deletion
- Request validation
- HTTP status codes
- Error handling
- API contract verification
- Security verification

## Out of Scope

The following test types are currently not implemented:

- UI Testing
- Performance Testing
- Load Testing
- Stress Testing
- Accessibility Testing
- Penetration Testing

---

# 3. Test Objectives

The API tests verify:

- Correct business behaviour
- Proper validation
- Authentication and authorization
- Correct HTTP status codes
- Response body correctness
- Error response consistency
- Database interaction through public API
- Regression after future changes

---

# 4. Test Levels

| Test Level | Status |
|------------|--------|
| Unit Tests | Planned |
| API Integration Tests | Implemented |
| End-to-End Tests | Planned |
| Performance Tests | Planned |

The current project focuses primarily on API Integration Testing using REST Assured.

---

# 5. Test Types

The following testing techniques are used:

- Functional Testing
- Validation Testing
- Negative Testing
- Security Testing
- Smoke Testing
- Regression Testing
- API Contract Testing

---

# 6. Test Design Techniques

The following test design techniques are applied when creating test cases.

## Boundary Value Analysis (BVA)

Examples:

- ownerName minimum length
- ownerName maximum length
- balance decimal precision

---

## Equivalence Partitioning (EP)

Examples:

Valid email

```
john@example.com
```

Invalid email

```
john
```

```
john@
```

```
@example.com
```

---

## Negative Testing

Examples:

- Missing request body
- Invalid JSON
- Invalid JWT
- Expired JWT
- Missing required fields

---

## Risk-Based Testing

The testing effort focuses primarily on business-critical functionality.

Highest priority areas:

- Authentication
- Authorization
- Account management
- Validation
- Error handling

---

# 7. Risk Assessment

| Area | Risk | Priority |
|------|------|----------|
| Authentication | High | Critical |
| Authorization | High | Critical |
| Account Creation | High | High |
| Account Retrieval | Medium | High |
| Account Deletion | High | High |
| Request Validation | Medium | Medium |
| Error Handling | Medium | Medium |

Testing effort is prioritised according to business risk.

---

# 8. API Coverage

## Authentication

### POST /api/v1/auth/login

### Happy Path

- Valid username
- Valid password

### Validation

- Username is null
- Username is empty
- Password is null
- Password is empty

### Authentication

- Invalid username
- Invalid password
- Malformed JSON

### Contract

- JWT returned
- Correct HTTP status
- Correct response structure

---

## Accounts

### POST /api/v1/accounts

### Happy Path

- Valid account creation

### Validation

- ownerName null
- ownerName empty
- ownerName blank
- ownerName too short
- ownerName too long

- email null
- email empty
- invalid email
- duplicate email

- balance null
- negative balance
- too many decimal places

### Security

- Missing JWT
- Invalid JWT
- Expired JWT

### Contract

- HTTP 201
- JSON response
- Response body correctness

---

### GET /api/v1/accounts/{id}

### Happy Path

- Existing account

### Business

- Account not found

### Validation

- Invalid ID
- Negative ID

### Security

- Missing JWT
- Invalid JWT

### Contract

- Correct response body

---

### DELETE /api/v1/accounts/{id}

### Happy Path

- Existing account

### Business

- Account not found

### Security

- Missing JWT
- Invalid JWT

---

# 9. Test Data

The framework uses two approaches for test data generation.

## Static Data

- Test users
- Authentication credentials

## Dynamic Data

Generated using DataFaker:

- Owner names
- Email addresses
- Account balances

Dynamic test data reduces conflicts between test executions.

---

# 10. Test Environment

Supported environments:

| Environment | Purpose |
|-------------|---------|
| Local | Developer workstation |
| Docker | Local integration testing |
| Jenkins | Continuous Integration |

Environment selection is controlled using Maven properties.

Example:

```
mvn verify -Denv=local
```

---

# 11. Continuous Integration

The project uses Jenkins Pipeline.

Pipeline stages:

1. Checkout
2. Build Backend
3. Start Docker Environment
4. Execute API Tests
5. Publish Test Reports

The goal is to execute automated API regression tests for every build.

---

# 12. Success Criteria

The build is considered successful when:

- All automated tests pass
- No unexpected HTTP responses occur
- No validation regressions are detected
- Authentication works correctly
- Jenkins pipeline finishes successfully

---

# 13. Future Improvements

Planned improvements:

- Testcontainers
- WireMock
- JaCoCo
- SonarQube
- GitHub Actions
- Performance Testing
- Contract Testing
- OWASP Dependency Check

---

# 14. Summary

This project follows a **Risk-Based Testing** approach.

API scenarios are designed using:

- Boundary Value Analysis
- Equivalence Partitioning
- Negative Testing
- Security Testing

The objective is to provide reliable automated regression tests for a RESTful banking application while maintaining high readability, scalability, and maintainability of the test framework.