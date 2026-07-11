# **Automated Testing of Grocery Store API** 🛒

## **Table of Contents** 📑
1. [Project Description](#project-description-)
2. [Architecture](#architecture)
3. [Technologies and Tools Used](#technologies-and-tools-used)
4. [Project Structure](#project-structure-)
5. [Features](#features-)
6. [How to run](#how-to-run)
7. [Documentation](#documentation-)
8. [Testing Approach](#testing-approach)
9. [Future Improvements](#future-improvements-)
---

## **Project Description** 
Money API Testing Framework is a Java-based API automation framework created to demonstrate
professional API testing practices for banking applications.

The project contains:

- Spring Boot REST API
- REST Assured automation framework
- JWT authentication
- PostgreSQL
- Docker
- Jenkins CI
---

## **Architecture**

```
REST Assured

↓

Spring Boot

↓

Service

↓

Repository

↓

PostgreSQL
```


---

## Technologies and Tools Used
1. **Java 21**
2. Spring Boot
3. REST Assured for API testing and validation
4. JUnit5
5. PostgreSQL
6. Flyway
7. Docker
8. Jenkins
9. Maven

---

## **Project Structure** 
Money<br>
├── app-backend<br>
├── testing-framework<br>
└── docs

---

## **Features** ✨

✔ JWT Authentication

✔ REST API Testing

✔ Docker Environment

✔ Jenkins Pipeline

✔ Database Migrations

✔ Validation Testing

✔ Error Handling

✔ Custom Assertions

✔ Test Data Builders

✔ DataFaker

---

## **How to run**
```
git clone ...

cd Money

docker compose up

mvn verify
```
---

## **Documentation** 
For more details see:

docs/TestStrategy.md

docs/Architecture.md

docs/TestCoverage.md

---

## Testing Approach

The project follows a risk-based testing approach. API scenarios are designed using boundary value analysis, equivalence partitioning, and negative testing techniques. Functional, validation, security, and regression scenarios are automated with REST Assured. Detailed test planning and coverage are documented separately in docs/TestStrategy.md.

---

## **Future Improvements** 🚀
- **Performance Testing**: Integrate tools like JMeter or k6 for load testing.
- **CI/CD Integration**: Automate test execution using Jenkins or GitHub Actions.
- **Reporting**: Generate detailed test reports for Java
- **Cross-Browser/Platform Testing**: Extend testing to different environments and configurations.

- Testcontainers

- WireMock

- JaCoCo

- SonarQube

- GitHub Actions

- Performance Tests

---
