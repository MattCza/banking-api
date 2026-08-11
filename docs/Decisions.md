# Decision Log

This document records the most important architectural and technical decisions made during the development of the **Money** project.

The purpose of this document is to explain **why** specific solutions were chosen, what problems they solved, and how the project evolved over time.

---

# ADR-001 | Project Vision 
**Date:** 2026-06-18
## Context

The goal of the project is to simulate a production-like banking REST API together 
with a professional API automation framework.

The project uses the following technologies:

- Java
- Spring Boot
- REST Assured
- Docker
- PostgreSQL
- Maven
- Jenkins
- JWT Authentication
- CI/CD
- Test Architecture

## Decision

The project consists of two independent parts:

- Banking backend
- API automation framework

The backend provides a realistic application under test, 
while the automation framework verifies its behavior.

## Consequences

- Building the project from scratch provides valuable practical experience.
- The project grows incrementally, 
making it easier to understand how architecture and automated tests evolve over time.
- Working on both the backend and the automation framework gives a broader perspective on software quality and testing.


---

# ADR-002 | Multi-module Maven Project

**Date:** 2026-06-20

### Context
The backend application and the automation framework serve different purposes and should evolve independently.

## Decision
Organize the project as a Maven multi-module application.

```
Money
│
├── app-backend
└── testing-framework
```

## Consequences
- Clear separation of responsibilities
- Independent module development
- Simpler build and CI configuration

---

# ADR-003 | Spring Boot REST API
**Date:** 2026-06-21

## Context
The project requires a backend exposing REST endpoints for automated testing.

## Decision
Implement the backend using Spring Boot and follow REST principles.

## Consequences
- Enterprise-standard technology stack
- Easy integration with REST Assured
- Realistic API implementation

---

# ADR-004 | PostgreSQL + Flyway

**Date:** 2026-06-23

## Context
The project requires a persistent relational database with version-controlled schema changes.

## Decision
Use PostgreSQL together with Flyway database migrations.

## Consequences
- Versioned database schema
- Repeatable deployments
- Easy environment setup

---

# ADR-005 | Docker Development Environment
**Date:** 2026-06-24

## Context
Every developer should work on the same application environment.

## Decision
Use Docker Compose to start:
- PostgreSQL
- Spring Boot Backend

## Consequences
- Consistent local environments
- Easier onboarding
- Repeatable executions

---

# ADR-006 | Jenkins Continuous Integration
**Date:** 2026-06-26

## Context
API tests should be executed automatically during every build.

## Decision
Implement a Jenkins pipeline responsible for:
- checkout source code
- build backend
- start Docker environment
- wait until backend becomes healthy
- execute REST Assured tests
- collect reports
- shutdown environment

## Consequences
- Automated API verification
- Faster feedback
- CI process similar to enterprise projects
---

# ADR-007 | REST Assured Test Framework
**Date:** 2026-06-27

## Context
As the number of API tests grows, the framework should remain easy to maintain and extend.

## Decision
Organize the automation framework into reusable layers:

- API Clients
- DTOs
- Data Factories
- Assertions
- Configuration
- Tests

## Consequences
- Better code reuse
- Cleaner architecture
- Easier implementation of new endpoints
---

# ADR-008 | Authentication Using JWT
**Date:** 2026-06-30

## Context
Some API endpoints require authenticated access.

## Decision
Secure the API using JWT authentication.

Authentication flow includes:

- Login endpoint
- JWT generation
- JWT validation filter
- Stateless authentication

## Consequences
- Stateless authentication
- Secure REST API
- Realistic enterprise authentication flow

---

# ADR-009 | Generic Response Assertions

**Date:** 2026-07-02

## Context
Many tests repeated the same HTTP response assertions.

## Decision
Introduce reusable ResponseAssertions containing common HTTP verifications such as:

- assertStatus()
- assertContentTypeJson()
- assertJsonResponse()

## Consequences
- Reduced code duplication
- Cleaner tests
- Centralized HTTP assertions

---

# ADR-010 | Dedicated Assertion Layers
**Date:** 2026-07-02

## Context
HTTP assertions, business assertions and validation assertions represent different responsibilities.

Keeping them together would reduce readability and violate the Single Responsibility Principle.

## Decision
Separate assertions into dedicated classes:

- ResponseAssertions
- ErrorAssertions
- ValidationAssertions
- AccountAssertions
- AuthAssertions

## Consequences
- Better separation of concerns
- Easier maintenance
- Improved readability
- Reusable assertion layer

---

# ADR-011 | Custom AuthenticationEntryPoint
**Date:** 2026-07-02

## Context
Authentication failures returned HTTP 403 Forbidden instead of HTTP 401 Unauthorized.

Additionally, authentication errors should follow the same JSON contract as the remaining API errors.

## Decision
Implement a custom JwtAuthenticationEntryPoint that:

- returns HTTP 401
- returns JSON responses
- reuses the common ErrorResponse DTO

## Consequences
- Correct HTTP semantics
- Consistent error responses
- Easier automated verification

---

# ADR-012 | Test Data Factories
**Date:** 2026-07-02

## Context
Tests contained hardcoded request objects, making them harder to read and maintain.

## Decision
Introduce Data Factory classes for creating test data.

Examples:

- AccountDataFactory
- LoginDataFactory

Tests describe business scenarios instead of object construction.

## Consequences
- Improved readability
- Reusable test data
- Easier maintenance

---

# ADR-013 | Test Strategy
**Date:** 2026-07-02

## Context
As the number of integration tests increased, maintaining a consistent test structure became increasingly important.

## Decision
Adopt the Arrange–Act–Assert (AAA) testing pattern.

Tests are written using the Given–When–Then convention:

- Given — Arrange
- When — Act
- Then — Assert

Each endpoint is verified using:

- Happy Path
- Negative scenarios
- Validation scenarios
- Authorization scenarios
- Business rule verification

## Consequences
- Consistent test structure
- Improved readability
- Easier onboarding
- Simpler future maintenance

---
# ADR-014 | Test Class Organization
**Date:** 2026-07-28

## Decision

The integration tests are organized into multiple smaller test classes 
based on their responsibility instead of keeping all scenarios in a single test class.

Example structure:

- `CreateAccountIT`
- `GetAccountIT`
- `DeleteAccountIT`
- `AccountValidationIT`
- `AccountSecurityIT`
- `AccountConcurrencyIT`

## Reason

As the project grows, a single test class becomes difficult to navigate and maintain. 
Grouping tests by responsibility improves readability, reduces class size, 
and makes it easier to locate, extend, and review test scenarios.

## Result

A cleaner and more maintainable test suite with clear separation of concerns.**

---


# Future Improvements

The following areas are planned for future iterations:

- Money transfers
- Transactions
- Testcontainers
- Allure Reports
- Parallel test execution
- Contract Testing
- Performance Testing
- GitHub Actions