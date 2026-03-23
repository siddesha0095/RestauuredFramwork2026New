package com.api.tests.users;

import com.api.framework.base.BaseTest;
import com.api.framework.config.ConfigManager;
import com.api.framework.constants.ApiConstants;
import com.api.framework.endpoints.UserEndpoints;
import com.api.framework.utils.JsonUtils;
import com.api.framework.utils.ResponseValidator;
import com.api.framework.utils.SpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * AdvancedRestAssuredTest - Advanced RestAssured features:
 *
 *  - JSON Schema validation
 *  - Custom logging filters (captured to string)
 *  - JsonPath complex expressions
 *  - Multiple query params
 *  - Response extraction (path, as, jsonPath)
 *  - Chained then() validations
 *  - Header extraction and assertion
 *  - Conditional validation (if field exists)
 *  - Custom spec with extra headers
 *  - Relaxed HTTPS validation
 */
public class AdvancedRestAssuredTest extends BaseTest {

    // ==================== JSON SCHEMA VALIDATION ====================

    @Test(description = "Validate GET /users/{id} response against JSON schema")
    public void testUserResponseMatchesJsonSchema() {
        logStep("GET /api/users/2 and validate against users-schema.json");
        Response response = UserEndpoints.getUserById(2);

        ResponseValidator.validateStatusCode200(response);

        // Schema file must exist in src/test/resources/schemas/
        response.then()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/user-response-schema.json"));

        logPass("Response matches JSON Schema");
    }

    @Test(description = "Validate GET /users list response against JSON schema")
    public void testUserListResponseMatchesJsonSchema() {
        logStep("GET /api/users?page=1 and validate against user-list-schema.json");
        Response response = UserEndpoints.getUsers(1);

        ResponseValidator.validateStatusCode200(response);

        response.then()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/user-list-response-schema.json"));

        logPass("User list response matches JSON Schema");
    }

    // ==================== RESPONSE EXTRACTION ====================

    @Test(description = "Extract response values using .path(), .jsonPath(), .as()")
    public void testMultipleExtractionMethods() {
        logStep("GET /api/users/1 and extract values multiple ways");
        Response response = UserEndpoints.getUserById(1);

        ResponseValidator.validateStatusCode200(response);

        // Method 1: .path()
        Integer idViaDotPath = response.path("data.id");
        assertThat(idViaDotPath).isEqualTo(1);

        // Method 2: .jsonPath().get()
        String emailViaJsonPath = response.jsonPath().getString("data.email");
        assertThat(emailViaJsonPath).contains("@");

        // Method 3: Extract entire data block as Map
        Map<String, Object> dataMap = response.jsonPath().getMap("data");
        assertThat(dataMap).containsKey("id")
                            .containsKey("email")
                            .containsKey("first_name");

        // Method 4: Full body as string → re-parsed with standalone JsonPath
        String body = response.getBody().asString();
        JsonPath jp = new JsonPath(body);
        String firstName = jp.getString("data.first_name");
        assertThat(firstName).isNotBlank();

        logPass("All extraction methods succeeded. firstName=" + firstName);
    }

    @Test(description = "Chain multiple then() assertions in a single test")
    public void testChainedThenAssertions() {
        logStep("GET /api/users?page=2 and chain multiple body/header assertions");
        ValidatableResponse validatableResponse = UserEndpoints.getUsers(2)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .time(lessThan(5000L))
                .body("page",        equalTo(2))
                .body("data",        notNullValue())
                .body("data.size()", greaterThan(0))
                .body("data[0].id",  notNullValue());

        logPass("All chained assertions passed for page=2");
    }

    // ==================== HEADER EXTRACTION ====================

    @Test(description = "Extract and assert response headers")
    public void testResponseHeaderExtraction() {
        logStep("GET /api/users and extract response headers");
        Response response = UserEndpoints.getUsers(1);

        ResponseValidator.validateStatusCode200(response);

        String contentType = response.getHeader("Content-Type");
        assertThat(contentType).isNotBlank().containsIgnoringCase("application/json");

        // Server header
        String server = response.getHeader("Server");
        logStep("Server header: " + server);

        // Print all headers to log
        response.getHeaders().forEach(header ->
                log.debug("Header: {}={}", header.getName(), header.getValue())
        );

        logPass("Content-Type header: " + contentType);
    }

    // ==================== CUSTOM LOGGING FILTERS ====================

    @Test(description = "Capture request/response log into string for assertion")
    public void testCustomLoggingFilterCapturesOutput() {
        logStep("Send request with custom logging filter capturing to ByteArrayOutputStream");

        ByteArrayOutputStream requestLog  = new ByteArrayOutputStream();
        ByteArrayOutputStream responseLog = new ByteArrayOutputStream();

        Response response = given()
                .spec(SpecBuilder.getReqResSpec())
                .filter(new RequestLoggingFilter(LogDetail.ALL, new PrintStream(requestLog)))
                .filter(new ResponseLoggingFilter(LogDetail.ALL, new PrintStream(responseLog)))
                .when()
                .get(ApiConstants.ReqRes.USERS + "?page=1");

        ResponseValidator.validateStatusCode200(response);

        String capturedRequest  = requestLog.toString();
        String capturedResponse = responseLog.toString();

        assertThat(capturedRequest).contains("GET");
        assertThat(capturedResponse).contains("200");

        logStep("Captured request log length: " + capturedRequest.length());
        logStep("Captured response log length: " + capturedResponse.length());
        logPass("Custom logging filter captured request and response logs");
    }

    // ==================== QUERY PARAMS ====================

    @Test(description = "Send multiple query params in a single request")
    public void testMultipleQueryParams() {
        logStep("GET /api/users with page=1 and per_page=3");
        Response response = UserEndpoints.getUsersWithPerPage(1, 3);

        ResponseValidator.validateStatusCode200(response);

        int perPage = response.jsonPath().getInt("per_page");
        assertThat(perPage).isEqualTo(3);

        List<Object> data = response.jsonPath().getList("data");
        assertThat(data).hasSizeLessThanOrEqualTo(3);

        logPass("Multiple query params applied. per_page=" + perPage);
    }

    // ==================== RELAXED HTTPS ====================

    @Test(description = "GET users using relaxed HTTPS spec — no cert validation")
    public void testRelaxedHttpsSpec() {
        logStep("GET /api/users using relaxed HTTPS specification");
        Response response = given()
                .spec(SpecBuilder.getRelaxedHttpsSpec(ConfigManager.getConfig().reqresBaseUrl()))
                .queryParam("page", 1)
                .when()
                .get(ApiConstants.ReqRes.USERS);

        ResponseValidator.validateStatusCode200(response);
        logPass("Relaxed HTTPS spec request succeeded");
    }

    // ==================== CUSTOM HEADERS ====================

    @Test(description = "Send request with custom correlation-id and user-agent headers")
    public void testCustomRequestHeaders() {
        String correlationId = "test-corr-" + System.currentTimeMillis();
        String userAgent     = "RestAssuredFramework/1.0";

        Map<String, String> customHeaders = new HashMap<>();
        customHeaders.put(ApiConstants.Headers.X_CORRELATION_ID, correlationId);
        customHeaders.put(ApiConstants.Headers.USER_AGENT, userAgent);

        logStep("GET /api/users/1 with custom headers: " + customHeaders);
        Response response = UserEndpoints.getUsersWithCustomHeaders(1, customHeaders);

        ResponseValidator.validateStatusCode200(response);
        logPass("Request with custom headers succeeded");
    }

    // ==================== RESPONSE TIME ASSERTIONS ====================

    @Test(description = "Assert response time using TimeUnit conversion")
    public void testResponseTimeInMilliseconds() {
        logStep("GET /api/users/1 and measure response time");
        Response response = UserEndpoints.getUserById(1);

        ResponseValidator.validateStatusCode200(response);

        long ms = response.timeIn(TimeUnit.MILLISECONDS);
        long sec = response.timeIn(TimeUnit.SECONDS);

        assertThat(ms).isLessThan(5000);
        log.info("Response time: {}ms / {}s", ms, sec);

        // Also use Hamcrest style
        response.then().time(lessThan(5000L));

        logPass("Response time assertion passed: " + ms + "ms");
    }

    // ==================== CONDITIONAL FIELD ASSERTION ====================

    @Test(description = "Assert optional 'error' field absent in successful response")
    public void testErrorFieldAbsentInSuccessResponse() {
        logStep("GET /api/users/1 — verify 'error' field is absent");
        Response response = UserEndpoints.getUserById(1);

        ResponseValidator.validateStatusCode200(response);

        String error = response.jsonPath().getString("error");
        assertThat(error).isNull();

        logPass("'error' field is absent in success response");
    }

    // ==================== JSON VALIDITY ====================

    @Test(description = "Verify response body is valid JSON")
    public void testResponseBodyIsValidJson() {
        logStep("GET /api/users/1 and validate body is valid JSON");
        Response response = UserEndpoints.getUserById(1);

        ResponseValidator.validateStatusCode200(response);

        String body = response.getBody().asString();
        assertThat(JsonUtils.isValidJson(body)).isTrue();

        logPass("Response body is valid JSON. Length=" + body.length() + " chars");
    }

    // ==================== GPATH COMPLEX ====================

    @Test(description = "Use GPath to find max user id in the list")
    public void testGPathMaxUserId() {
        logStep("GET /api/users?page=1 and find max id via GPath");
        Response response = UserEndpoints.getUsers(1);

        ResponseValidator.validateStatusCode200(response);

        List<Integer> ids   = response.jsonPath().getList("data.id");
        int maxId = ids.stream().mapToInt(Integer::intValue).max().orElse(0);

        assertThat(maxId).isGreaterThan(0);
        logPass("Max user id on page 1: " + maxId);
    }
}
