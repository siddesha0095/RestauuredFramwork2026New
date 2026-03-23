package com.api.framework.utils;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.SoftAssertions;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * ResponseValidator - Utility class for reusable response validation methods.
 * Combines Hamcrest matchers, AssertJ assertions, and JSON Schema validation.
 */
public class ResponseValidator {

    private static final Logger log = LogManager.getLogger(ResponseValidator.class);

    private ResponseValidator() {}

    // ==================== STATUS CODE VALIDATIONS ====================

    public static void validateStatusCode(Response response, int expectedCode) {
        log.info("Validating status code: expected={}, actual={}", expectedCode, response.statusCode());
        assertThat(response.statusCode())
                .as("Status code mismatch")
                .isEqualTo(expectedCode);
    }

    public static void validateStatusCode200(Response response) {
        validateStatusCode(response, 200);
    }

    public static void validateStatusCode201(Response response) {
        validateStatusCode(response, 201);
    }

    public static void validateStatusCode204(Response response) {
        validateStatusCode(response, 204);
    }

    public static void validateStatusCode400(Response response) {
        validateStatusCode(response, 400);
    }

    public static void validateStatusCode401(Response response) {
        validateStatusCode(response, 401);
    }

    public static void validateStatusCode404(Response response) {
        validateStatusCode(response, 404);
    }

    // ==================== CONTENT TYPE VALIDATIONS ====================

    public static void validateContentType(Response response, String expectedContentType) {
        log.info("Validating content type: expected={}", expectedContentType);
        assertThat(response.getContentType())
                .as("Content-Type mismatch")
                .containsIgnoringCase(expectedContentType);
    }

    public static void validateJsonContentType(Response response) {
        validateContentType(response, "application/json");
    }

    // ==================== RESPONSE TIME VALIDATIONS ====================

    public static void validateResponseTime(Response response, long maxMillis) {
        long responseTime = response.timeIn(TimeUnit.MILLISECONDS);
        log.info("Response time: {}ms (max allowed: {}ms)", responseTime, maxMillis);
        assertThat(responseTime)
                .as("Response time exceeded %d ms", maxMillis)
                .isLessThan(maxMillis);
    }

    public static void validateResponseTimeUnder5Seconds(Response response) {
        validateResponseTime(response, 5000);
    }

    // ==================== HEADER VALIDATIONS ====================

    public static void validateHeaderPresent(Response response, String headerName) {
        log.info("Validating header present: {}", headerName);
        assertThat(response.getHeader(headerName))
                .as("Header '%s' should be present", headerName)
                .isNotNull();
    }

    public static void validateHeaderValue(Response response, String headerName, String expectedValue) {
        log.info("Validating header: {}={}", headerName, expectedValue);
        assertThat(response.getHeader(headerName))
                .as("Header '%s' value mismatch", headerName)
                .isEqualToIgnoringCase(expectedValue);
    }

    // ==================== JSON BODY VALIDATIONS ====================

    public static void validateFieldNotNull(Response response, String jsonPath) {
        Object value = response.jsonPath().get(jsonPath);
        assertThat(value)
                .as("Field '%s' should not be null", jsonPath)
                .isNotNull();
    }

    public static void validateFieldValue(Response response, String jsonPath, Object expectedValue) {
        Object actual = response.jsonPath().get(jsonPath);
        log.info("Validating field '{}': expected={}, actual={}", jsonPath, expectedValue, actual);
        assertThat(actual)
                .as("Field '%s' value mismatch", jsonPath)
                .isEqualTo(expectedValue);
    }

    public static void validateFieldContains(Response response, String jsonPath, String substring) {
        String actual = response.jsonPath().getString(jsonPath);
        assertThat(actual)
                .as("Field '%s' should contain '%s'", jsonPath, substring)
                .containsIgnoringCase(substring);
    }

    public static void validateListNotEmpty(Response response, String jsonPath) {
        List<?> list = response.jsonPath().getList(jsonPath);
        assertThat(list)
                .as("List at '%s' should not be empty", jsonPath)
                .isNotNull()
                .isNotEmpty();
    }

    public static void validateListSize(Response response, String jsonPath, int expectedSize) {
        List<?> list = response.jsonPath().getList(jsonPath);
        assertThat(list)
                .as("List at '%s' size mismatch", jsonPath)
                .hasSize(expectedSize);
    }

    // ==================== JSON SCHEMA VALIDATION ====================

    public static void validateJsonSchema(Response response, String schemaFileName) {
        log.info("Validating JSON schema: {}", schemaFileName);
        response.then().assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/" + schemaFileName));
    }

    // ==================== SOFT ASSERTIONS ====================

    /**
     * Soft assertion container — collect all failures before throwing.
     */
    public static void validateWithSoftAssertions(Response response,
                                                   int expectedStatusCode,
                                                   String expectedContentType,
                                                   long maxResponseTimeMs) {
        SoftAssertions soft = new SoftAssertions();

        soft.assertThat(response.statusCode())
                .as("Status code").isEqualTo(expectedStatusCode);
        soft.assertThat(response.getContentType())
                .as("Content-Type").containsIgnoringCase(expectedContentType);
        soft.assertThat(response.timeIn(TimeUnit.MILLISECONDS))
                .as("Response time").isLessThan(maxResponseTimeMs);

        soft.assertAll();
    }

    // ==================== COMBINED STANDARD VALIDATION ====================

    /**
     * Full standard API response validation: status + content-type + time
     */
    public static void validateSuccessResponse(Response response) {
        validateStatusCode200(response);
        validateJsonContentType(response);
        validateResponseTimeUnder5Seconds(response);
    }

    public static void validateCreatedResponse(Response response) {
        validateStatusCode201(response);
        validateJsonContentType(response);
        validateResponseTimeUnder5Seconds(response);
    }

    // ==================== LOGGING HELPER ====================

    public static void logResponseDetails(Response response) {
        log.info("===== RESPONSE DETAILS =====");
        log.info("Status Code   : {}", response.statusCode());
        log.info("Content-Type  : {}", response.getContentType());
        log.info("Response Time : {}ms", response.timeIn(TimeUnit.MILLISECONDS));
        log.info("Body          : {}", response.asPrettyString());
        log.info("============================");
    }
}
