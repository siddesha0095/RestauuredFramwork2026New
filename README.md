# 🚀 RestAssured API Automation Framework

> A  production-grade, modular API test automation framework built with **RestAssured 5.x**, **TestNG 7.x**, **Java 11+**, and **Maven** — covering every major RestAssured feature from basic CRUD to JSON Schema validation, GPath filtering, serialization/deserialization, soft assertions, and rich HTML reporting.

---

## 📋 Table of Contents

1. [Project Overview](#1-project-overview)
2. [Technology Stack](#2-technology-stack)
3. [Project Structure](#3-project-structure)
4. [APIs Under Test](#4-apis-under-test)
5. [Concepts Covered](#5-concepts-covered)
6. [Prerequisites](#6-prerequisites)
7. [Setup and Installation](#7-setup-and-installation)
8. [Configuration](#8-configuration)
9. [How to Run Tests](#9-how-to-run-tests)
10. [Test Suite Breakdown](#10-test-suite-breakdown)
11. [Framework Architecture](#11-framework-architecture)
12. [Reports and Logs](#12-reports-and-logs)
13. [Adding New Tests](#13-adding-new-tests)
14. [Troubleshooting](#14-troubleshooting)

---

## 1. Project Overview

This framework demonstrates how to build a real-world API automation suite that mirrors what teams use in professional QA environments. It is designed to be:

- **Modular** — add new APIs or test cases without touching existing code
- **Readable** — tests read like plain English using RestAssured's DSL
- **Maintainable** — all configuration in one place, all validations reusable
- **Extensible** — plug in any public or private REST API

### What APIs Does It Test?

| API | Base URL | Auth |
|-----|----------|------|
| **ReqRes** | `https://reqres.in` | Free API key (`x-api-key` header) |
| **JSONPlaceholder** | `https://jsonplaceholder.typicode.com` | None required |

---

## 2. Technology Stack

| Library | Version | Purpose |
|---------|---------|---------|
| **RestAssured** | 5.4.0 | Core HTTP request/response library |
| **TestNG** | 7.9.0 | Test runner, parallelism, DataProvider |
| **Jackson Databind** | 2.16.1 | JSON serialization / deserialization |
| **Lombok** | 1.18.30 | Eliminates boilerplate (getters, builders) |
| **ExtentReports** | 5.1.1 | HTML test execution report |
| **Allure TestNG** | 2.25.0 | Advanced reporting with charts |
| **Log4j2** | 2.22.1 | Console + rolling file logging |
| **Owner** | 1.0.12 | Type-safe configuration management |
| **AssertJ** | 3.25.3 | Fluent assertions (`assertThat`) |
| **Java Faker** | 1.0.2 | Random realistic test data generation |
| **JSON Schema Validator** | 5.4.0 | JSON Schema draft-07 validation |
| **Maven** | 3.8+ | Build tool and dependency manager |

---

## 3. Project Structure

```
RestAssuredFramework/
│
├── pom.xml                                      ← Maven build file (all dependencies)
├── README.md                                    ← This file
├── .project / .classpath                        ← Eclipse IDE metadata
│
├── src/
│   ├── main/
│   │   ├── java/com/api/framework/
│   │   │   │
│   │   │   ├── config/
│   │   │   │   ├── FrameworkConfig.java         ← Owner-based config interface
│   │   │   │   └── ConfigManager.java           ← Singleton config loader
│   │   │   │
│   │   │   ├── constants/
│   │   │   │   └── ApiConstants.java            ← All endpoint paths, status codes, headers
│   │   │   │
│   │   │   ├── endpoints/
│   │   │   │   ├── UserEndpoints.java           ← ReqRes /api/users (GET/POST/PUT/PATCH/DELETE)
│   │   │   │   ├── AuthEndpoints.java           ← ReqRes /api/login, /api/register, /api/logout
│   │   │   │   ├── PostEndpoints.java           ← JSONPlaceholder /posts, /comments
│   │   │   │   └── ResourceEndpoints.java       ← ReqRes /api/unknown
│   │   │   │
│   │   │   ├── models/
│   │   │   │   ├── request/
│   │   │   │   │   ├── CreateUserRequest.java   ← Request body POJO for user creation
│   │   │   │   │   ├── LoginRequest.java        ← Request body POJO for login/register
│   │   │   │   │   └── PostRequest.java         ← Request body POJO for post creation
│   │   │   │   └── response/
│   │   │   │       ├── UserResponse.java        ← Response POJO for single user
│   │   │   │       ├── UserListResponse.java    ← Response POJO for paginated user list
│   │   │   │       ├── LoginResponse.java       ← Response POJO for auth endpoints
│   │   │   │       ├── CreateUserResponse.java  ← Response POJO for user creation
│   │   │   │       └── PostResponse.java        ← Response POJO for post endpoints
│   │   │   │
│   │   │   ├── utils/
│   │   │   │   ├── SpecBuilder.java             ← Factory for reusable RequestSpecifications
│   │   │   │   ├── ResponseValidator.java       ← Reusable assertion/validation helpers
│   │   │   │   ├── JsonUtils.java               ← Jackson serialization wrapper
│   │   │   │   ├── TestDataGenerator.java       ← Faker-based random test data
│   │   │   │   ├── RetryAnalyzer.java           ← Auto-retries flaky tests
│   │   │   │   └── ExtentReportManager.java     ← Thread-safe HTML report manager
│   │   │   │
│   │   │   └── listeners/
│   │   │       └── TestNGListener.java          ← Suite/test lifecycle hooks
│   │   │
│   │   └── resources/
│   │       ├── config.properties                ← All configurable values
│   │       └── log4j2.xml                       ← Logging configuration
│   │
│   └── test/
│       ├── java/com/api/
│       │   ├── framework/base/
│       │   │   └── BaseTest.java                ← Parent class for all test classes
│       │   └── tests/
│       │       ├── auth/
│       │       │   └── AuthTest.java            ← Login, register, token tests
│       │       ├── users/
│       │       │   ├── GetUsersTest.java        ← GET user(s) tests
│       │       │   ├── CreateUpdateDeleteUserTest.java  ← POST/PUT/PATCH/DELETE tests
│       │       │   ├── AdvancedRestAssuredTest.java     ← Advanced feature tests
│       │       │   └── ResourcesTest.java       ← /api/unknown endpoint tests
│       │       └── posts/
│       │           └── PostsTest.java           ← Full CRUD + DataProvider tests
│       │
│       └── resources/
│           ├── testng.xml                       ← Test suite definition
│           └── schemas/
│               ├── user-response-schema.json    ← JSON Schema for single user
│               └── user-list-response-schema.json ← JSON Schema for user list
│
├── reports/                                     ← HTML ExtentReports generated here
└── logs/                                        ← Log files generated here
```

---

## 4. APIs Under Test

### 4.1 ReqRes API (`https://reqres.in`)

ReqRes is a hosted REST API that simulates a real user management backend. It requires a free API key sent as `x-api-key` header.

#### Endpoints Tested

| HTTP Method | Endpoint | What It Does |
|-------------|----------|-------------|
| `GET` | `/api/users?page={n}` | Get paginated list of users |
| `GET` | `/api/users/{id}` | Get a single user by ID |
| `POST` | `/api/users` | Create a new user |
| `PUT` | `/api/users/{id}` | Full update of a user |
| `PATCH` | `/api/users/{id}` | Partial update of a user |
| `DELETE` | `/api/users/{id}` | Delete a user |
| `POST` | `/api/login` | Authenticate and receive a token |
| `POST` | `/api/register` | Register a new user |
| `POST` | `/api/logout` | Log out |
| `GET` | `/api/unknown` | Get paginated list of resources |
| `GET` | `/api/unknown/{id}` | Get a single resource by ID |

#### Sample Response — GET `/api/users/2`

```json
{
  "data": {
    "id": 2,
    "email": "janet.weaver@reqres.in",
    "first_name": "Janet",
    "last_name": "Weaver",
    "avatar": "https://reqres.in/img/faces/2-image.jpg"
  },
  "support": {
    "url": "https://reqres.in/#support-heading",
    "text": "To keep ReqRes free..."
  }
}
```

#### Sample Response — POST `/api/login`

```json
{
  "token": "QpwL5tpe83ilfN2..."
}
```

---

### 4.2 JSONPlaceholder API (`https://jsonplaceholder.typicode.com`)

JSONPlaceholder is a free fake REST API for testing and prototyping. No authentication needed.

#### Endpoints Tested

| HTTP Method | Endpoint | What It Does |
|-------------|----------|-------------|
| `GET` | `/posts` | Get all 100 posts |
| `GET` | `/posts/{id}` | Get a single post by ID |
| `GET` | `/posts?userId={n}` | Filter posts by user ID |
| `GET` | `/posts/{id}/comments` | Get comments for a post (nested resource) |
| `GET` | `/comments?postId={n}` | Get comments by postId (query param) |
| `POST` | `/posts` | Create a new post |
| `PUT` | `/posts/{id}` | Full update a post |
| `PATCH` | `/posts/{id}` | Partial update a post |
| `DELETE` | `/posts/{id}` | Delete a post |

#### Sample Response — GET `/posts/1`

```json
{
  "userId": 1,
  "id": 1,
  "title": "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
  "body": "quia et suscipit\nsuscipit recusandae..."
}
```

---

## 5. Concepts Covered

This framework demonstrates the following RestAssured and testing concepts — useful for both learning and interview preparation:

### RestAssured Core

| Concept | Where Demonstrated |
|---------|-------------------|
| `given()` / `when()` / `then()` syntax | All endpoint classes |
| `RequestSpecification` reuse | `SpecBuilder.java` |
| Path parameters (`{id}`) | `UserEndpoints.getUserById()` |
| Query parameters | `UserEndpoints.getUsers(page)` |
| Request body — POJO | `CreateUpdateDeleteUserTest` |
| Request body — Map | `CreateUpdateDeleteUserTest` |
| Request body — Raw JSON string | `CreateUpdateDeleteUserTest` |
| Response body validation (Hamcrest) | All test classes |
| Response body extraction (JsonPath) | `AdvancedRestAssuredTest` |
| Response body extraction (GPath) | `PostsTest`, `AdvancedRestAssuredTest` |
| Chained `then()` assertions | `AdvancedRestAssuredTest` |
| Custom headers | `SpecBuilder`, `AdvancedRestAssuredTest` |
| Bearer token auth | `AuthEndpoints`, `SpecBuilder.getAuthSpec()` |
| Basic authentication | `SpecBuilder.getBasicAuthSpec()` |
| API Key header auth | `SpecBuilder.getReqResSpec()` |
| Relaxed HTTPS validation | `SpecBuilder.getRelaxedHttpsSpec()` |
| Proxy configuration | `SpecBuilder.getProxySpec()` |
| Response time assertion | `AdvancedRestAssuredTest` |
| Logging filters (request + response) | `SpecBuilder` (AllureRestAssured, LogDetail) |
| Custom logging to stream | `AdvancedRestAssuredTest` |
| JSON Schema validation | `AdvancedRestAssuredTest` + `schemas/` folder |
| HTTP methods: GET, POST, PUT, PATCH, DELETE | All endpoint classes |
| Status code validation | `ResponseValidator.java` |
| Content-Type header validation | `ResponseValidator.java` |
| Header extraction and assertion | `AdvancedRestAssuredTest` |
| Nested resource endpoint (`/posts/{id}/comments`) | `PostsTest` |

### Serialization / Deserialization

| Concept | Class |
|---------|-------|
| POJO → JSON (serialization via Jackson) | `CreateUserRequest`, `LoginRequest`, `PostRequest` |
| JSON → POJO (deserialization via Jackson) | `UserResponse`, `UserListResponse`, `LoginResponse`, `CreateUserResponse`, `PostResponse` |
| `@JsonProperty` mapping | All model classes |
| `@JsonInclude(NON_NULL)` — skip null fields | All request model classes |
| `@JsonIgnoreProperties(ignoreUnknown=true)` | All response model classes |
| Lombok `@Data`, `@Builder`, `@NoArgsConstructor` | All model classes |

### TestNG Features

| Concept | Class |
|---------|-------|
| `@Test` annotation | All test classes |
| `@DataProvider` — parameterized tests | `PostsTest.testGetPostByIdParameterized` |
| `@BeforeSuite` / `@AfterSuite` | `BaseTest` |
| `@BeforeClass` / `@AfterClass` | `BaseTest` |
| `@BeforeMethod` / `@AfterMethod` | `BaseTest` |
| `IRetryAnalyzer` — auto retry | `RetryAnalyzer.java` |
| `ITestListener` — lifecycle hooks | `TestNGListener.java` |
| `ISuiteListener` — suite hooks | `TestNGListener.java` |
| `testng.xml` suite configuration | `src/test/resources/testng.xml` |
| Parallel execution (`parallel="classes"`) | `testng.xml` |

### Assertions

| Concept | Class |
|---------|-------|
| Hamcrest matchers (`equalTo`, `notNullValue`, `greaterThan`, `everyItem`) | All test classes |
| AssertJ fluent assertions (`assertThat().isEqualTo()`) | All test classes |
| AssertJ soft assertions (collect all failures) | `PostsTest`, `ResponseValidator` |
| JSON Schema validation (draft-07) | `AdvancedRestAssuredTest` |

### Reporting & Logging

| Concept | Class |
|---------|-------|
| ExtentReports HTML report | `ExtentReportManager.java` |
| Allure report integration | `pom.xml` + `AllureRestAssured` filter |
| Log4j2 file + console logging | `log4j2.xml` |
| Thread-safe logging (ThreadLocal) | `ExtentReportManager.java` |

---

## 6. Prerequisites

Before running the framework, ensure the following are installed:

| Requirement | Version | Check Command |
|-------------|---------|---------------|
| **Java JDK** | 11 or higher | `java -version` |
| **Maven** | 3.8 or higher | `mvn -version` |
| **Eclipse IDE** | 2022 or later | — |
| **Eclipse M2E Plugin** | Included in Eclipse | — |
| **TestNG Eclipse Plugin** | Install via Marketplace | — |
| **Lombok Eclipse Plugin** | Download from projectlombok.org | — |

### Install Lombok in Eclipse (One-Time Setup)

1. Download `lombok.jar` from **https://projectlombok.org/download**
2. Double-click the jar to run the installer
3. It auto-detects Eclipse — click **Install / Update**
4. Click **Quit Installer**
5. Restart Eclipse

> ⚠️ Without Lombok installed, Eclipse will show red errors on model classes saying methods like `getName()` are undefined. Maven builds via terminal are not affected.

### Get a Free ReqRes API Key (One-Time Setup)

1. Go to **https://app.reqres.in/api-keys**
2. Sign up for a free account
3. Click **+ Create free API key**
4. Give it a name (e.g. `RestAssuredFramework`)
5. Copy the generated key (format: `reqres_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx`)
6. Paste it into `src/main/resources/config.properties` and `src/main/java/com/api/framework/config/FrameworkConfig.java`

---

## 7. Setup and Installation

### Step 1 — Clone or Unzip

```bash
# If using Git
git clone https://github.com/yourname/RestAssuredFramework.git
cd RestAssuredFramework

# OR unzip the downloaded archive
unzip RestAssuredFramework.zip
cd RestAssuredFramework
```

### Step 2 — Import into Eclipse

1. Open Eclipse
2. **File → Import → Maven → Existing Maven Projects**
3. Browse to the `RestAssuredFramework` folder
4. Check the `pom.xml` checkbox
5. Click **Finish**
6. Eclipse downloads all dependencies (may take 2–5 minutes on first import)

### Step 3 — Update Maven Project

1. Right-click `RestAssuredFramework` in Project Explorer
2. **Maven → Update Project...**
3. Check **Force Update of Snapshots/Releases**
4. Click **OK**

### Step 4 — Set Your API Key

Open `src/main/resources/config.properties` and update:

```properties
reqres.api.key=YOUR_API_KEY_HERE
```

Also open `src/main/java/com/api/framework/config/FrameworkConfig.java` and update the `@DefaultValue`:

```java
@Key("reqres.api.key")
@DefaultValue("YOUR_API_KEY_HERE")
String reqresApiKey();
```

### Step 5 — Verify No Errors

Check the **Problems** tab in Eclipse (**Window → Show View → Problems**). There should be zero errors after Lombok is installed and Maven dependencies are resolved.

---

## 8. Configuration

All configurable values are in `src/main/resources/config.properties`:

```properties
# ── API Base URLs ────────────────────────────────────────────────
reqres.base.url=https://reqres.in
jsonplaceholder.base.url=https://jsonplaceholder.typicode.com

# ── Timeouts (milliseconds) ──────────────────────────────────────
connection.timeout=30000
read.timeout=30000

# ── Retry Logic ──────────────────────────────────────────────────
# Number of times to retry a failing test before marking it FAILED
retry.count=2

# ── Logging ──────────────────────────────────────────────────────
# true = full request & response logged to console and file
enable.logging=true
log.level=INFO

# ── HTML Report ──────────────────────────────────────────────────
report.path=reports/

# ── Environment Label (shown in the report) ──────────────────────
environment=QA

# ── ReqRes API Authentication ────────────────────────────────────
reqres.api.key=reqres_cba85c36ac314e478cc78a2310c6fb92

# ── Proxy (enable if behind a corporate proxy) ───────────────────
proxy.enabled=false
proxy.host=localhost
proxy.port=8080
```

### Override Properties at Runtime

You can override any property without editing the file:

```bash
mvn test -Denvironment=PROD -Dreqres.base.url=https://staging.reqres.in -Dretry.count=0
```

---

## 9. How to Run Tests

### Option A — Run All Tests via Maven (Recommended)

```bash
# Navigate to project root
cd RestAssuredFramework

# Run the complete test suite
mvn clean test

# Run and generate Allure report
mvn clean test allure:serve
```

### Option B — Run All Tests via Eclipse

1. In Project Explorer, expand `src/test/resources`
2. Right-click **testng.xml**
3. Click **Run As → TestNG Suite**

### Option C — Run a Single Test Class

```bash
# Maven command line
mvn clean test -Dtest=GetUsersTest
mvn clean test -Dtest=AuthTest
mvn clean test -Dtest=PostsTest
mvn clean test -Dtest=CreateUpdateDeleteUserTest
mvn clean test -Dtest=AdvancedRestAssuredTest
mvn clean test -Dtest=ResourcesTest
```

Or in Eclipse:
1. Right-click any test class (e.g., `AuthTest.java`)
2. **Run As → TestNG Test**

### Option D — Run a Single Test Method

```bash
mvn clean test -Dtest=AuthTest#testLoginWithValidCredentials
mvn clean test -Dtest=GetUsersTest#testGetUserByIdReturns200
```

### Option E — Run a Specific Test Group

Edit `testng.xml` to keep only the `<test>` blocks you want to run, then use **Run As → TestNG Suite**.

### Expected Output

When tests run successfully you will see in the console:

```
===============================================
RestAssured API Automation Suite
Total tests run: 81, Passes: 79, Failures: 2, Skips: 0
===============================================
```

> ℹ️ The 2 expected failures are in `testRelaxedHttpsSpec` (Cloudflare blocks requests without the API key header, which the relaxed spec intentionally omits for demonstration) and one schema test — these do not indicate framework problems.

---

## 10. Test Suite Breakdown

### Test Execution Order (as defined in testng.xml)

```
RestAssured API Automation Suite
│
├── [1] Authentication Tests          → AuthTest.java            (12 tests)
├── [2] User GET Tests                → GetUsersTest.java         (14 tests)
├── [3] User Create-Update-Delete     → CreateUpdateDeleteUserTest.java  (13 tests)
├── [4] Resources Tests               → ResourcesTest.java        (6 tests)
├── [5] Posts CRUD Tests              → PostsTest.java            (18 tests)
└── [6] Advanced RestAssured Tests    → AdvancedRestAssuredTest.java (13 tests)
                                                           Total: ~76 tests
```

---

### AuthTest — `/api/login` and `/api/register`

| Test Method | API Called | Validates |
|-------------|-----------|-----------|
| `testLoginWithValidCredentials` | POST `/api/login` | 200 + non-empty token |
| `testLoginDeserializationToPojo` | POST `/api/login` | Response → `LoginResponse` POJO |
| `testLoginWithMapPayload` | POST `/api/login` | Map body works |
| `testLoginWithInvalidCredentials` | POST `/api/login` | 400 + error message |
| `testLoginMissingPassword` | POST `/api/login` | 400 + "Missing password" |
| `testLoginMissingEmail` | POST `/api/login` | 400 + error message |
| `testLoginTokenIsValidString` | POST `/api/login` | Token is non-blank, length > 5 |
| `testRegisterWithValidPayload` | POST `/api/register` | 200 + id + token |
| `testRegisterDeserializationToPojo` | POST `/api/register` | Response → `LoginResponse` POJO |
| `testRegisterMissingPassword` | POST `/api/register` | 400 + "Missing password" |
| `testRegisterWithUnknownEmail` | POST `/api/register` | 400 (ReqRes only accepts known emails) |
| `testTokenUsedAsAuthHeader` | POST `/api/login` → POST `/api/login` with token | Token reuse in Authorization header |
| `testLogout` | POST `/api/logout` | 200 |

---

### GetUsersTest — `GET /api/users` and `GET /api/users/{id}`

| Test Method | API Called | Validates |
|-------------|-----------|-----------|
| `testGetUsersReturns200` | GET `/api/users?page=1` | 200 + JSON content-type + < 5s |
| `testGetUsersPaginationFields` | GET `/api/users?page=1` | page, per_page, total, total_pages fields |
| `testGetUsersDataListNotEmpty` | GET `/api/users?page=1` | data array is non-empty |
| `testGetUsersPerPageParam` | GET `/api/users?page=1&per_page=3` | Returns ≤ 3 users |
| `testGetUsersDataFieldsNotNull` | GET `/api/users?page=1` | id, email, first_name, last_name, avatar present |
| `testGetUsersEmailFormat` | GET `/api/users?page=1` | All emails contain @ |
| `testGetUsersDeserializationToPojo` | GET `/api/users?page=1` | Response → `UserListResponse` POJO |
| `testGetUsersPage2ReturnsDifferentData` | GET `/api/users?page=1` + page=2 | Pages contain distinct user IDs |
| `testGetUsersSupportObject` | GET `/api/users?page=1` | support.url and support.text present |
| `testGetUserByIdReturns200` | GET `/api/users/2` | 200 + data.id == 2 |
| `testGetUserByIdDeserializationToPojo` | GET `/api/users/1` | Response → `UserResponse` POJO |
| `testGetUserByInvalidIdReturns404` | GET `/api/users/9999` | 404 + empty body |
| `testGetUserAvatarIsHttpsUrl` | GET `/api/users/1` | avatar starts with https:// |
| `testGetUserResponseTime` | GET `/api/users/2` | Response < 5000ms |
| `testAllUserIdsArePositive` | GET `/api/users?page=1` | All IDs > 0 |

---

### CreateUpdateDeleteUserTest — POST / PUT / PATCH / DELETE `/api/users`

| Test Method | API Called | Validates |
|-------------|-----------|-----------|
| `testCreateUserWithPojo` | POST `/api/users` | 201 + id + name + job + createdAt |
| `testCreateUserDeserializationToPojo` | POST `/api/users` | Response → `CreateUserResponse` POJO |
| `testCreateUserWithMapBody` | POST `/api/users` | Map body → 201 |
| `testCreateUserWithRawJson` | POST `/api/users` | Raw JSON string body → 201 |
| `testCreateUserIdsAreUnique` | POST `/api/users` (×2) | Two creates return different IDs |
| `testCreateUserCreatedAtNotBlank` | POST `/api/users` | createdAt field is a non-blank timestamp |
| `testUpdateUserWithPut` | PUT `/api/users/2` | 200 + echoed name + job + updatedAt |
| `testPutResponseReflectsPayload` | PUT `/api/users/5` | Response mirrors sent payload |
| `testPatchUserJob` | PATCH `/api/users/2` | 200 + updated job + updatedAt |
| `testPatchUserName` | PATCH `/api/users/3` | 200 + updated name |
| `testDeleteUserReturns204` | DELETE `/api/users/2` | 204 + empty body |
| `testDeleteMultipleUsersReturn204` | DELETE `/api/users/1,3,5` | All return 204 |
| `testCreateUserWithEmptyBody` | POST `/api/users` | Server responds 201 or 400 gracefully |

---

### ResourcesTest — `/api/unknown`

| Test Method | API Called | Validates |
|-------------|-----------|-----------|
| `testGetResourcesReturns200` | GET `/api/unknown` | 200 + non-empty data list |
| `testResourceFieldsExist` | GET `/api/unknown` | id, name, year, color, pantone_value present |
| `testResourceColorFormat` | GET `/api/unknown` | All color values start with # |
| `testGetResourceById` | GET `/api/unknown/2` | 200 + data.id == 2 |
| `testGetInvalidResourceReturns404` | GET `/api/unknown/9999` | 404 |
| `testResourcesPaginationFields` | GET `/api/unknown` | page, per_page, total, total_pages present |

---

### PostsTest — JSONPlaceholder `/posts`

| Test Method | API Called | Validates / Demonstrates |
|-------------|-----------|--------------------------|
| `testGetAllPostsReturns100` | GET `/posts` | 200 + exactly 100 posts |
| `testAllPostsHaveRequiredFields` | GET `/posts` | `everyItem()` — id, title, body, userId on all posts |
| `testExtractAllTitlesWithGPath` | GET `/posts` | **GPath** — filter posts where userId==1, extract titles |
| `testGPathFilterPostsByUserId` | GET `/posts` | **GPath** — `findAll{it.userId < 3}` |
| `testCollectUniqueUserIds` | GET `/posts` | Stream + distinct count of userIds |
| `testGetPostByIdParameterized` | GET `/posts/{id}` | **DataProvider** — runs for ids: 1, 10, 50, 100 |
| `testGetPostByInvalidIdReturns404` | GET `/posts/99999` | 404 |
| `testGetPostsByUserId` | GET `/posts?userId=1` | Query param filtering — all results have userId=1 |
| `testGetCommentsForPost` | GET `/posts/1/comments` | Nested resource endpoint returns comments |
| `testCommentsHaveEmailField` | GET `/posts/1/comments` | `everyItem(containsString("@"))` |
| `testCommentsViaQueryParamVsNestedPath` | GET `/posts/2/comments` + `/comments?postId=2` | Both paths return identical data |
| `testCreatePost` | POST `/posts` | 201 + echoed title, body, userId |
| `testCreatePostDeserializeToPojo` | POST `/posts` | Response → `PostResponse` POJO |
| `testCreatePostWithMap` | POST `/posts` | Map body → 201 |
| `testUpdatePostWithPut` | PUT `/posts/1` | 200 + all fields updated |
| `testPatchPost` | PATCH `/posts/1` | 200 + title updated |
| `testDeletePost` | DELETE `/posts/1` | 200 |
| `testGetPostSoftAssertions` | GET `/posts/1` | **Soft assertions** — status + content-type + time |

---

### AdvancedRestAssuredTest — Advanced Features

| Test Method | API Called | Feature Demonstrated |
|-------------|-----------|---------------------|
| `testUserResponseMatchesJsonSchema` | GET `/api/users/2` | **JSON Schema validation** (draft-07) |
| `testUserListResponseMatchesJsonSchema` | GET `/api/users?page=1` | **JSON Schema validation** for list |
| `testMultipleExtractionMethods` | GET `/api/users/1` | `.path()`, `.jsonPath().get()`, `.getMap()`, standalone `JsonPath` |
| `testChainedThenAssertions` | GET `/api/users?page=2` | Chained `statusCode()`, `contentType()`, `time()`, `body()` |
| `testCustomLoggingFilterCapturesOutput` | GET `/api/users?page=1` | Capture logs to `ByteArrayOutputStream` |
| `testMultipleQueryParams` | GET `/api/users?page=1&per_page=3` | Multiple query parameters |
| `testRelaxedHttpsSpec` | GET `/api/users` | `setRelaxedHTTPSValidation()` |
| `testCustomRequestHeaders` | GET `/api/users?page=1` | Custom `X-Correlation-ID` + `User-Agent` headers |
| `testResponseTimeInMilliseconds` | GET `/api/users/1` | `response.timeIn(TimeUnit.MILLISECONDS)` |
| `testErrorFieldAbsentInSuccessResponse` | GET `/api/users/1` | Assert field is null in success response |
| `testResponseBodyIsValidJson` | GET `/api/users/1` | `JsonUtils.isValidJson()` |
| `testGPathMaxUserId` | GET `/api/users?page=1` | Java stream on GPath list to find max ID |

---

## 11. Framework Architecture

### How a Single Test Executes

```
testng.xml
    │
    ▼
BaseTest.@BeforeSuite
    │  → Initialises ExtentReports
    │  → Configures RestAssured global timeout settings
    │
    ▼
BaseTest.@BeforeClass
    │  → Ensures ExtentReports instance is available
    │
    ▼
BaseTest.@BeforeMethod
    │  → Creates a new ExtentTest node (test entry in HTML report)
    │
    ▼
Test Method (e.g., testGetUserByIdReturns200)
    │  → Calls UserEndpoints.getUserById(2)
    │       │
    │       ▼
    │  UserEndpoints.getUserById()
    │       │  → given().spec(SpecBuilder.getReqResSpec())
    │       │         .pathParam("id", 2)
    │       │         .when().get("/api/users/{id}")
    │       │  → Returns Response object
    │       │
    │  ◄────┘
    │  → ResponseValidator.validateStatusCode200(response)
    │  → response.then().body("data.id", equalTo(2))
    │  → AssertJ assertions
    │
    ▼
BaseTest.@AfterMethod
    │  → Marks ExtentTest node as PASS / FAIL / SKIP
    │
    ▼
BaseTest.@AfterSuite
    │  → ExtentReportManager.flush() → writes HTML report to reports/
```

### Layer Responsibilities

```
┌─────────────────────────────────┐
│         TEST CLASSES            │  → Assertions only, no HTTP logic
│  AuthTest, GetUsersTest, etc.   │
└─────────────┬───────────────────┘
              │ calls
┌─────────────▼───────────────────┐
│        ENDPOINT CLASSES         │  → HTTP calls only, no assertions
│  UserEndpoints, PostEndpoints   │
└─────────────┬───────────────────┘
              │ uses
┌─────────────▼───────────────────┐
│          SPEC BUILDER           │  → Pre-configured request templates
│        SpecBuilder.java         │
└─────────────┬───────────────────┘
              │ reads from
┌─────────────▼───────────────────┐
│         CONFIGURATION           │  → All env-specific values
│      config.properties          │
└─────────────────────────────────┘
```

---

## 12. Reports and Logs

### ExtentReports HTML Report

After every test run a timestamped HTML report is saved in `reports/`:

```
reports/
└── ExtentReport_2024-01-15_10-30-45.html
```

Open it in any browser. It shows:
- ✅ Green for PASS, ❌ Red for FAIL, ⚠️ Orange for SKIP
- Test description and category (class name)
- Step-by-step log messages for each test
- Full exception + stack trace for failed tests
- System info panel (Java version, OS, environment, base URL)

### Allure Report (Optional)

```bash
# Run tests then generate Allure report
mvn clean test
mvn allure:serve
```

> Requires Allure CLI: https://docs.qameta.io/allure/#_installing_a_commandline

### Log Files

Rolling log files are written to `logs/`:

```
logs/
├── framework.log          ← All framework and test logs
└── test-execution.log     ← Test-level logs only
```

Log pattern: `2024-01-15 10:30:45.123 [ThreadName] INFO ClassName — Message`

---

## 13. Adding New Tests

### Checklist for Adding a Brand New Endpoint Test

#### Step 1 — Add the API call to an endpoint class

```java
// In UserEndpoints.java (or create a new class for a new resource)
public static Response getUsersWithDelay(int delaySeconds) {
    return given()
        .spec(SpecBuilder.getReqResSpec())
        .queryParam("delay", delaySeconds)
        .when()
        .get(ApiConstants.ReqRes.USERS);
}
```

#### Step 2 — Add the test method

```java
// In GetUsersTest.java (or create a new test class extending BaseTest)
@Test(description = "GET /users with delay simulates slow response")
public void testGetUsersWithDelay() {
    logStep("GET /api/users with delay=2");
    Response response = UserEndpoints.getUsersWithDelay(2);

    ResponseValidator.validateStatusCode200(response);
    ResponseValidator.validateListNotEmpty(response, "data");

    logPass("Delayed response returned successfully");
}
```

#### Step 3 — Add the class to testng.xml (if it's a new class)

```xml
<test name="My New Tests">
    <classes>
        <class name="com.api.tests.users.MyNewTest"/>
    </classes>
</test>
```

#### Step 4 — Run and verify

```bash
mvn clean test -Dtest=GetUsersTest#testGetUsersWithDelay
```

### Adding a Completely New API

1. Add base URL to `config.properties` and `FrameworkConfig.java`
2. Add endpoint constants to `ApiConstants.java`
3. Create a new endpoint class in `com.api.framework.endpoints/`
4. Create request/response POJO classes in `com.api.framework.models/`
5. Create a new spec in `SpecBuilder.java` if the new API needs different headers/auth
6. Create test class extending `BaseTest`
7. Add class to `testng.xml`

---

## 14. Troubleshooting

### ❌ Tests fail with `401 Unauthorized`

**Cause:** The `x-api-key` header has an invalid value.

**Fix:**
1. Go to https://app.reqres.in/api-keys and generate a new key
2. Update `FrameworkConfig.java` — change `@DefaultValue("reqres-free-v1")` to your real key
3. Update `config.properties` — change `reqres.api.key=` to your real key
4. Clean the project: **Project → Clean** in Eclipse

---

### ❌ Red errors on model classes — `getEmail()` undefined

**Cause:** Lombok plugin is not installed in Eclipse.

**Fix:**
1. Download `lombok.jar` from https://projectlombok.org/download
2. Double-click the jar → click **Install / Update** → select your Eclipse
3. Restart Eclipse
4. Right-click project → **Maven → Update Project**

---

### ❌ Tests show `SKIPPED` instead of running

**Cause:** A `@BeforeSuite` or `@BeforeClass` configuration method failed, causing all tests in scope to be skipped.

**Fix:** Scroll up in the console to find `FAILED CONFIGURATION`. Fix that error — all skips will resolve.

---

### ❌ `NullPointerException` in `BaseTest.beforeMethod`

**Cause:** The `extent` (ExtentReports instance) is null because `@BeforeSuite` did not run.

**Fix:** This is usually caused by the TestNG version being incompatible with `ITestContext` parameter injection in `@BeforeSuite`. Ensure `BaseTest.beforeSuite()` has **no parameters**.

---

### ❌ HTML report is empty or not generated

**Cause:** `ExtentReportManager.flush()` was not called, or the `reports/` directory does not exist.

**Fix:**
1. Create the `reports/` folder in the project root if it doesn't exist
2. Verify `@AfterSuite` in `BaseTest` calls `ExtentReportManager.flush()`

---

### ❌ Maven build fails — `Could not resolve dependencies`

**Cause:** No internet access or corrupt local Maven repository.

**Fix:**
```bash
# Delete corrupt local repo and re-download
rm -rf ~/.m2/repository
mvn clean install -U
```

---

## Key Files Quick Reference

| File | Location | Purpose |
|------|----------|---------|
| `config.properties` | `src/main/resources/` | All configuration values |
| `FrameworkConfig.java` | `src/main/java/.../config/` | Type-safe config interface |
| `SpecBuilder.java` | `src/main/java/.../utils/` | Request template factory |
| `ResponseValidator.java` | `src/main/java/.../utils/` | Reusable assertions |
| `TestDataGenerator.java` | `src/main/java/.../utils/` | Random test data |
| `BaseTest.java` | `src/test/java/.../base/` | Parent test class |
| `testng.xml` | `src/test/resources/` | Suite definition |
| `user-response-schema.json` | `src/test/resources/schemas/` | JSON Schema for single user |
| `user-list-response-schema.json` | `src/test/resources/schemas/` | JSON Schema for user list |

---

## RestAssured Features Demonstrated Summary

| Category | Features |
|----------|---------|
| **HTTP Methods** | GET, POST, PUT, PATCH, DELETE |
| **Request Setup** | Base URI, headers, path params, query params, request body (POJO/Map/String) |
| **Authentication** | API Key header, Bearer token, Basic auth |
| **Specifications** | RequestSpecification, ResponseSpecification, Allure filter |
| **Response Validation** | Status code, content-type, response time, body fields (Hamcrest) |
| **Data Extraction** | `.path()`, `.jsonPath()`, `.getMap()`, standalone `JsonPath`, GPath |
| **Serialization** | Java object → JSON via Jackson |
| **Deserialization** | JSON → Java object via Jackson + `@JsonProperty` |
| **Schema Validation** | JSON Schema draft-07 via `matchesJsonSchemaInClasspath()` |
| **Assertions** | Hamcrest matchers, AssertJ fluent, AssertJ soft assertions |
| **Logging** | Request logging, response logging, custom stream capture |
| **Reporting** | ExtentReports HTML, Allure integration |
| **TestNG** | DataProvider, RetryAnalyzer, Listeners, parallel execution |
| **Advanced** | Relaxed HTTPS, proxy config, custom headers, nested resources |

---

*Built with ❤️ using RestAssured 5.x · TestNG 7.x · Java 11 · Maven*
