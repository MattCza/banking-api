# Decision Log

This document records the most important architectural and technical decisions made during the development of the **Money** project.

The purpose of this document is to explain **why** specific solutions were chosen, what problems they solved, and how the project evolved over time.

---

# ADR-001 | Project Vision

**Date:** 2026-06-18

## Goal

The goal of this project is to simulate a production-like banking REST API together with a professional API automation framework.

The project is intended to demonstrate skills expected from a QA Automation Engineer, including:

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

Instead of creating only automated tests, the project will contain both:

- Banking backend
- Independent automation framework

## Reason

QA Automation Engineers often work with existing applications, but understanding backend architecture significantly improves the quality of automated tests and communication with developers.

---

# ADR-002 | Multi-module Maven Project

**Date:** 2026-06-20

## Problem

The backend application and the automation framework are two different applications with different responsibilities.

## Decision

Created a Maven multi-module project:

```
Money
│
├── app-backend
└── testing-framework
```

## Reason

The backend and the testing framework should evolve independently while remaining inside the same repository.

## Result

- Better separation of responsibilities
- Cleaner architecture
- Easier CI/CD pipeline

---

# ADR-003 | Spring Boot REST API

**Date:** 2026-06-21

## Decision

Implemented the backend using Spring Boot.

## Reason

Spring Boot is one of the most commonly used Java frameworks in enterprise environments and banking systems.

The API follows REST principles.

## Result

Implemented initial endpoints for account management.

---

# ADR-004 | PostgreSQL + Flyway

**Date:** 2026-06-23

## Problem

The project required a persistent database with version-controlled schema.

## Decision

Selected PostgreSQL together with Flyway migrations.

## Reason

PostgreSQL is widely used in enterprise environments.

Flyway allows database schema evolution through versioned migrations.

## Result

- Database versioning
- Repeatable deployments
- Easy environment setup

---

# ADR-005 | Docker Development Environment

**Date:** 2026-06-24

## Problem

Every developer should work against exactly the same environment.

## Decision

Created a Docker Compose environment containing:

- PostgreSQL
- Spring Boot Backend

## Reason

Docker eliminates environment differences between machines.

## Result

- Repeatable executions
- Easier onboarding
- Stable local development

---

# ADR-006 | Jenkins Continuous Integration

**Date:** 2026-06-26

## Problem

API tests should execute automatically after every build.

## Decision

Created Jenkins Pipeline responsible for:

- checkout source code
- build backend
- start Docker environment
- wait until backend becomes healthy
- execute REST Assured tests
- collect reports
- shutdown environment

## Reason

The pipeline simulates a typical CI process used in enterprise projects.

## Result

Fully automated API verification.

---

# ADR-007 | REST Assured Test Framework

**Date:** 2026-06-27

## Decision

Designed the automation framework using reusable layers.

Project structure includes:

- API Clients
- DTOs
- Data Factories
- Assertions
- Configuration
- Tests

## Reason

The framework should remain readable and easily extendable as new endpoints are added.

## Result

Reusable and maintainable API tests.

---

# ADR-008 | Authentication Using JWT

**Date:** 2026-06-30

## Problem

Protected endpoints require authentication.

## Decision

Implemented JWT authentication.

Authentication flow:

- Login endpoint
- JWT generation
- JWT validation filter
- Stateless authentication

## Reason

JWT is commonly used for securing REST APIs.

## Result

Authenticated API requests without server-side sessions.

---

# ADR-009 | Generic Response Assertions

**Date:** 2026-07-02

## Problem

Each test duplicated HTTP assertions.

Example:

- Status Code
- Content-Type

## Decision

Introduced reusable ResponseAssertions.

Main methods:

- assertStatus()
- assertContentTypeJson()
- assertJsonResponse()

## Reason

HTTP response verification should be centralized.

## Result

Cleaner tests and less duplicated code.

---

# ADR-010 | Dedicated Assertion Layers

**Date:** 2026-07-02

## Problem

HTTP assertions, business assertions and validation assertions represent different responsibilities.

## Decision

Separated assertions into dedicated classes.

```
ResponseAssertions
ErrorAssertions
ValidationAssertions
AccountAssertions
AuthAssertions
```

## Reason

Each assertion class should have one responsibility.

## Result

Better maintainability and cleaner test code.

---

# ADR-011 | Custom AuthenticationEntryPoint

**Date:** 2026-07-02

## Problem

Invalid login attempts returned HTTP 403 Forbidden.

Authentication failures should return HTTP 401 Unauthorized.

Additionally, authentication errors should follow the same JSON contract as all other API errors.

## Decision

Implemented custom JwtAuthenticationEntryPoint.

The component:

- returns HTTP 401
- returns JSON
- reuses the common ErrorResponse DTO

## Reason

REST APIs should distinguish authentication failures (401) from authorization failures (403).

## Result

- Correct HTTP semantics
- Unified API error format
- Easier automated verification

---

# ADR-012 | Test Data Factories

**Date:** 2026-07-02

## Problem

Tests contained hardcoded request data.

## Decision

Introduced Data Factory classes.

Examples:

- AccountDataFactory
- LoginDataFactory

## Reason

Tests should describe business scenarios instead of implementation details.

Example:

Instead of

```java
new LoginRequest("admin", "wrongPassword")
```

tests now use

```java
LoginDataFactory.invalidPassword()
```

## Result

Improved readability and easier maintenance.

---

# ADR-013 | Test Strategy

**Date:** 2026-07-02

## Decision

The project follows the Arrange–Act–Assert (AAA) pattern for all automated tests.

Each endpoint is verified using:

- Happy Path
- Negative scenarios
- Validation scenarios
- Authorization scenarios
- Business rule verification

## Reason

A consistent testing strategy makes tests easier to understand and extend.

## Result

A scalable and predictable test suite.

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