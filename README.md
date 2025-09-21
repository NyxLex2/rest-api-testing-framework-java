# REST API Testing Framework

A comprehensive, production-ready API automation testing framework built with Java 17, Rest Assured, and TestNG. Designed for testing Player API endpoints with roleEnum-based access control, comprehensive validation, and detailed reporting.

##  Features

- **Modern Technology Stack**: Java 17, Rest Assured 5.3.2, TestNG 7.8.0
- **Clean Architecture**: SOLID principles with clear separation of concerns
- **Comprehensive Testing**: Positive, negative, boundary, and roleEnum-based access tests
- **Parallel Execution**: Configurable thread-based parallel test execution (3 threads by default)
- **Advanced Reporting**: Allure integration with detailed reports, attachments, and steps
- **JSON Schema Validation**: Automated response validation against JSON schemas
- **Test Data Generation**: Faker-based realistic test data with uniqueness guarantees
- **Multi-Environment Support**: Dev, QA, and Production configurations
- **Docker Support**: Containerized test execution with Docker Compose
- **CI/CD Ready**: Jenkins and GitHub Actions integration
- **Production-Ready**: Comprehensive logging, error handling, and monitoring

## API Endpoints Covered

- **POST** `/player/create` - Player creation with validation
- **PUT** `/player/update/{id}` - Player updates with roleEnum-based access
- **DELETE** `/player/delete` - Player deletion with business rules
- **POST** `/player/get` - Get player by ID
- **GET** `/player/get/all` - Get all players

### Role-Based Access Control
- **Supervisor**: Can update all users but cannot delete itself
- **Admin**: Can only manage users and itself, cannot manage supervisors or other admins
- **User**: Can only update itself

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- Docker (optional, for containerized execution)

### Installation
```bash
git clone <repository-url>
cd rest-api-testing-framework-java
mvn clean compile
```

#### Local Execution
```bash
# Run all regression tests
mvn clean test

# Run smoke tests only
mvn clean test -Dgroups=smoke

# Run tests against specific environment
mvn clean test -Dtest.environment=qa -Dbase.url=http://qa-server:8080

# Run with custom thread count
mvn clean test -Dthread.count=5

# Run specific test groups
mvn clean test -Dgroups="negative,boundary"
```


### Allure Reports
Generate and view detailed Allure reports:

```bash
# Generate report
mvn allure:report

# Serve report (opens browser)
mvn allure:serve

```