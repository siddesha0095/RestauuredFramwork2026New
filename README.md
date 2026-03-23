# RestAssured API Automation Framework

A production-grade, modular API test automation framework built with **RestAssured 5.x**, **TestNG 7.x**, and **Java 11+**, following industry best practices.

---

## 📁 Project Structure

```
RestAssuredFramework/
├── src/
│   ├── main/java/com/api/framework/
│   │   ├── auth/                         # (extendable — OAuth2, JWT helpers)
│   │   ├── config/
│   │   │   ├── FrameworkConfig.java      # Owner-based type-safe config interface
│   │   │   └── ConfigManager.java        # Singleton config accessor
│   │   ├── constants/
│   │   │   └── ApiConstants.java         # Endpoints, status codes, headers, fields
│   │   ├── endpoints/
│   │   │   ├── UserEndpoints.java        # ReqRes /users API client
│   │   │   ├── AuthEndpoints.java        # ReqRes /login, /register, /logout
│   │   │   ├── PostEndpoints.java        # JSONPlaceholder /posts API client
│   │   │   └── ResourceEndpoints.java    # ReqRes /unknown API client
│   │   ├── listeners/
│   │   │   └── TestNGListener.java       # Suite/test lifecycle hooks
│   │   ├── models/
│   │   │   ├── request/
│   │   │   │   ├── CreateUserRequest.java
│   │   │   │   ├── LoginRequest.java
│   │   │   │   └── PostRequest.java
│   │   │   └── response/
│   │   │       ├── UserResponse.java
│   │   │       ├── UserListResponse.java
│   │   │       ├── LoginResponse.java
│   │   │       ├── CreateUserResponse.java
│   │   │       └── PostResponse.java
│   │   └── utils/
│   │       ├── SpecBuilder.java           # RequestSpecification factory
│   │       ├── ResponseValidator.java     # Reusable validation methods
│   │       ├── JsonUtils.java             # Jackson serialization/deserialization
│   │       ├── TestDataGenerator.java     # Faker-based test data generation
│   │       ├── RetryAnalyzer.java         # TestNG IRetryAnalyzer
│   │       └── ExtentReportManager.java   # Thread-safe ExtentReports manager
│   │
│   ├── main/resources/
│   │   ├── config.properties              # Framework configuration
│   │   └── log4j2.xml                     # Logging configuration
│   │
│   └── test/
│       ├── java/com/api/
│       │   ├── framework/base/
│       │   │   └── BaseTest.java          # Parent test class (hooks, reporting)
│       │   └── tests/
│       │       ├── auth/
│       │       │   └── AuthTest.java      # Login, Register, Token tests
│       │       ├── users/
│       │       │   ├── GetUsersTest.java              # GET user tests
│       │       │   ├── CreateUpdateDeleteUserTest.java # POST/PUT/PATCH/DELETE
│       │       │   ├── AdvancedRestAssuredTest.java   # Schema, logging, GPath
│       │       │   └── ResourcesTest.java             # /unknown endpoint tests
│       │       └── posts/
│       │           └── PostsTest.java     # JSONPlaceholder CRUD + DataProvider
│       └── resources/
│           ├── testng.xml                 # TestNG suite configuration
│           └── schemas/
│               ├── user-response-schema.json
│               └── user-list-response-schema.json
├── reports/                               # Generated HTML test reports
├── logs/                                  # Log files
├── pom.xml
├── .project                               # Eclipse project metadata
├── .classpath                             # Eclipse classpath
└── README.md
```

---

## 🚀 Quick Start

### Prerequisites
- **Java 11+** installed and `JAVA_HOME` set
- **Maven 3.8+** installed
- **Eclipse IDE** with:
  - M2Eclipse plugin (Maven integration)
  - TestNG plugin for Eclipse

### Import into Eclipse
1. Open Eclipse → `File → Import → Existing Maven Projects`
2. Browse to the `RestAssuredFramework` folder
3. Click **Finish** — Eclipse will auto-download dependencies
4. Right-click `pom.xml` → `Maven → Update Project`

### Run Tests

**Run all tests via Maven:**
```bash
mvn clean test
```

**Run a specific test class:**
```bash
mvn clean test -Dtest=GetUsersTest
```

**Run a specific test suite:**
```bash
mvn clean test -DsuiteXmlFile=src/test/resources/testng.xml
```

**Override configuration via system property:**
```bash
mvn clean test -Denvironment=PROD -Dbase.url=https://reqres.in
```

**Run in Eclipse:**
- Right-click `testng.xml` → `Run As → TestNG Suite`
- Or right-click any test class → `Run As → TestNG Test`

---

## 🔧 Configuration

Edit `src/main/resources/config.properties`:

| Property | Default | Description |
|---|---|---|
| `reqres.base.url` | `https://reqres.in` | ReqRes API base URL |
| `jsonplaceholder.base.url` | `https://jsonplaceholder.typicode.com` | JSONPlaceholder base URL |
| `connection.timeout` | `30000` | HTTP connection timeout (ms) |
| `read.timeout` | `30000` | HTTP read timeout (ms) |
| `retry.count` | `2` | Auto-retry count for flaky tests |
| `enable.logging` | `true` | Enable full request/response logging |
| `environment` | `QA` | Environment label in reports |
| `proxy.enabled` | `false` | Enable HTTP proxy |
| `proxy.host` | `localhost` | Proxy host |
| `proxy.port` | `8080` | Proxy port |

---

## 📊 Reports

### ExtentReports (HTML)
After each run, an HTML report is generated in `reports/`:
```
reports/ExtentReport_2024-01-15_10-30-45.html
```
Open in any browser — includes pass/fail status, logs, and system info.

### Allure Reports
```bash
mvn allure:serve
```
Generates and opens Allure report in the browser (requires Allure CLI installed).

### Logs
All logs are written to `logs/`:
- `framework.log` — full framework logs
- `test-execution.log` — test-level logs

---

## 🧩 Framework Features

| Feature | Implementation |
|---|---|
| **Request Specification** | `SpecBuilder` — factory for reusable specs |
| **Response Validation** | `ResponseValidator` — Hamcrest + AssertJ |
| **JSON Schema Validation** | `rest-assured-json-schema-validator` |
| **Serialization** | Jackson — POJO ↔ JSON |
| **Deserialization** | Jackson — Response → POJO |
| **Test Data Generation** | Java Faker |
| **Configuration** | Owner library — type-safe properties |
| **Logging** | Log4j2 — console + rolling file |
| **Reporting** | ExtentReports 5 + Allure |
| **Retry Logic** | TestNG `IRetryAnalyzer` |
| **Parameterized Tests** | TestNG `@DataProvider` |
| **Soft Assertions** | AssertJ `SoftAssertions` |
| **Listeners** | TestNG `ITestListener` + `ISuiteListener` |
| **Authentication** | Bearer token, Basic auth, API Key |
| **GPath / JsonPath** | Complex response extraction |
| **Proxy Support** | Via `SpecBuilder.getProxySpec()` |
| **HTTPS Relaxation** | Via `SpecBuilder.getRelaxedHttpsSpec()` |

---

## 🏗️ Adding New Tests

1. Create a new endpoint class in `com.api.framework.endpoints/`
2. Create request/response POJOs in `com.api.framework.models/`
3. Create test class extending `BaseTest` in `com.api.tests/`
4. Add the test class to `testng.xml`

---

## 🌐 APIs Used

| API | Base URL | Purpose |
|---|---|---|
| ReqRes | `https://reqres.in` | User CRUD, Auth |
| JSONPlaceholder | `https://jsonplaceholder.typicode.com` | Posts, Comments |

---

## 📦 Key Dependencies

| Dependency | Version | Purpose |
|---|---|---|
| `rest-assured` | 5.4.0 | API testing core |
| `testng` | 7.9.0 | Test runner |
| `jackson-databind` | 2.16.1 | JSON serialization |
| `lombok` | 1.18.30 | Boilerplate reduction |
| `extentreports` | 5.1.1 | HTML reporting |
| `allure-testng` | 2.25.0 | Allure reporting |
| `log4j2` | 2.22.1 | Logging |
| `owner` | 1.0.12 | Config management |
| `assertj` | 3.25.3 | Fluent assertions |
| `javafaker` | 1.0.2 | Test data generation |
| `json-schema-validator` | 5.4.0 | JSON Schema validation |
