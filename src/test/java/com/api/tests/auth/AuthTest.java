package com.api.tests.auth;

import com.api.framework.base.BaseTest;
import com.api.framework.endpoints.AuthEndpoints;
import com.api.framework.models.request.LoginRequest;
import com.api.framework.models.response.LoginResponse;
import com.api.framework.utils.JsonUtils;
import com.api.framework.utils.ResponseValidator;
import com.api.framework.utils.RetryAnalyzer;
import com.api.framework.utils.TestDataGenerator;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * AuthTest - Tests for /api/login and /api/register
 *
 * Covers:
 *  - Successful login / register (token extraction)
 *  - POJO request serialization
 *  - Map request
 *  - Invalid credentials — 400 with error message
 *  - Missing password — 400
 *  - Missing email — 400
 *  - Token is non-blank and reusable
 *  - Token-based auth header
 */
public class AuthTest extends BaseTest {

    // ==================== LOGIN ====================

    @Test(description = "POST /login with valid credentials returns 200 and token",
          retryAnalyzer = RetryAnalyzer.class)
    public void testLoginWithValidCredentials() {
        LoginRequest request = LoginRequest.builder()
                .email("eve.holt@reqres.in")
                .password("cityslicka")
                .build();

        logStep("POST /api/login with valid credentials (POJO)");
        Response response = AuthEndpoints.login(request);

        ResponseValidator.validateStatusCode200(response);
        ResponseValidator.validateJsonContentType(response);

        response.then().body("token", notNullValue());

        String token = response.jsonPath().getString("token");
        assertThat(token).isNotBlank();

        logPass("Login successful. Token=" + token);
    }

    @Test(description = "POST /login — deserialize response to LoginResponse POJO")
    public void testLoginDeserializationToPojo() {
        LoginRequest request = LoginRequest.builder()
                .email("eve.holt@reqres.in")
                .password("cityslicka")
                .build();

        logStep("POST /api/login and deserialize to LoginResponse");
        Response response = AuthEndpoints.login(request);

        ResponseValidator.validateStatusCode200(response);

        LoginResponse loginResponse = JsonUtils.fromResponse(response, LoginResponse.class);
        assertThat(loginResponse.getToken()).isNotBlank();

        logPass("LoginResponse POJO — token=" + loginResponse.getToken());
    }

    @Test(description = "POST /login with Map payload returns 200 and token")
    public void testLoginWithMapPayload() {
        Map<String, String> payload = TestDataGenerator.getValidReqResLoginPayload();

        logStep("POST /api/login with Map payload");
        Response response = AuthEndpoints.login(payload);

        ResponseValidator.validateStatusCode200(response);
        response.then().body("token", notNullValue());

        logPass("Login with Map returned token");
    }

    @Test(description = "POST /login with invalid credentials returns 400 with error message")
    public void testLoginWithInvalidCredentials() {
        Map<String, String> payload = TestDataGenerator.getInvalidLoginPayload();

        logStep("POST /api/login with invalid credentials");
        Response response = AuthEndpoints.login(payload);

        ResponseValidator.validateStatusCode400(response);

        String errorMsg = response.jsonPath().getString("error");
        assertThat(errorMsg).isNotBlank();

        logPass("400 returned with error: " + errorMsg);
    }

    @Test(description = "POST /login with missing password returns 400")
    public void testLoginMissingPassword() {
        LoginRequest request = LoginRequest.builder()
                .email("eve.holt@reqres.in")
                .build(); // no password

        logStep("POST /api/login — missing password");
        Response response = AuthEndpoints.login(request);

        ResponseValidator.validateStatusCode400(response);
        response.then().body("error", equalTo("Missing password"));

        logPass("400 returned for missing password");
    }

    @Test(description = "POST /login with missing email returns 400")
    public void testLoginMissingEmail() {
        LoginRequest request = LoginRequest.builder()
                .password("somepassword")
                .build(); // no email

        logStep("POST /api/login — missing email");
        Response response = AuthEndpoints.login(request);

        ResponseValidator.validateStatusCode400(response);

        String error = response.jsonPath().getString("error");
        assertThat(error).isNotBlank();

        logPass("400 returned. Error: " + error);
    }

    @Test(description = "Extracted token is valid and non-empty string")
    public void testLoginTokenIsValidString() {
        LoginRequest request = LoginRequest.builder()
                .email("eve.holt@reqres.in")
                .password("cityslicka")
                .build();

        logStep("POST /api/login and verify token format");
        Response response = AuthEndpoints.login(request);

        ResponseValidator.validateStatusCode200(response);

        String token = response.jsonPath().getString("token");
        assertThat(token)
                .isNotBlank()
                .hasSizeGreaterThan(5);

        logPass("Token is valid. Length=" + token.length());
    }

    // ==================== REGISTER ====================

    @Test(description = "POST /register with valid payload returns 200 with token and id")
    public void testRegisterWithValidPayload() {
        Map<String, String> payload = TestDataGenerator.getValidReqResRegisterPayload();

        logStep("POST /api/register with valid payload");
        Response response = AuthEndpoints.register(payload);

        ResponseValidator.validateStatusCode200(response);

        response.then()
                .body("id",    notNullValue())
                .body("token", notNullValue());

        logPass("Register succeeded. id=" + response.jsonPath().getInt("id")
                + ", token=" + response.jsonPath().getString("token"));
    }

    @Test(description = "POST /register — deserialize to LoginResponse POJO")
    public void testRegisterDeserializationToPojo() {
        Map<String, String> payload = TestDataGenerator.getValidReqResRegisterPayload();

        logStep("POST /api/register and deserialize to LoginResponse");
        Response response = AuthEndpoints.register(payload);

        ResponseValidator.validateStatusCode200(response);

        LoginResponse registerResponse = JsonUtils.fromResponse(response, LoginResponse.class);
        assertThat(registerResponse.getToken()).isNotBlank();
        assertThat(registerResponse.getId()).isNotNull().isGreaterThan(0);

        logPass("Register POJO — id=" + registerResponse.getId()
                + ", token=" + registerResponse.getToken());
    }

    @Test(description = "POST /register with missing password returns 400")
    public void testRegisterMissingPassword() {
        LoginRequest request = LoginRequest.builder()
                .email("sydney@fife")
                .build();

        logStep("POST /api/register — missing password");
        Response response = AuthEndpoints.register(request);

        ResponseValidator.validateStatusCode400(response);
        response.then().body("error", equalTo("Missing password"));

        logPass("400 returned for register without password");
    }

    @Test(description = "POST /register with unknown email returns 400")
    public void testRegisterWithUnknownEmail() {
        LoginRequest request = LoginRequest.builder()
                .email(TestDataGenerator.getRandomEmail())
                .password(TestDataGenerator.getRandomPassword())
                .build();

        logStep("POST /api/register with unknown email (ReqRes only accepts predefined emails)");
        Response response = AuthEndpoints.register(request);

        // ReqRes returns 400 for unknown emails
        ResponseValidator.validateStatusCode400(response);

        String error = response.jsonPath().getString("error");
        assertThat(error).isNotBlank();

        logPass("400 returned for unknown email. Error: " + error);
    }

    // ==================== TOKEN-BASED AUTH ====================

    @Test(description = "Use extracted login token in subsequent request header")
    public void testTokenUsedAsAuthHeader() {
        // Step 1: Login and extract token
        LoginRequest loginRequest = LoginRequest.builder()
                .email("eve.holt@reqres.in")
                .password("cityslicka")
                .build();

        logStep("Step 1: Login to get token");
        Response loginResponse = AuthEndpoints.login(loginRequest);
        ResponseValidator.validateStatusCode200(loginResponse);
        String token = loginResponse.jsonPath().getString("token");
        assertThat(token).isNotBlank();

        // Step 2: Use token in next request
        logStep("Step 2: Send request with token in Authorization header. Token=" + token);
        Response tokenResponse = AuthEndpoints.loginWithToken(token);

        // ReqRes doesn't validate token on this endpoint — just verify request goes through
        int statusCode = tokenResponse.statusCode();
        assertThat(statusCode).isIn(200, 400); // either is acceptable from ReqRes

        logPass("Token-based auth header test complete. Status=" + statusCode);
    }

    // ==================== LOGOUT ====================

    @Test(description = "POST /logout — verify response (ReqRes returns 200)")
    public void testLogout() {
        logStep("POST /api/logout");
        Response response = AuthEndpoints.logout();

        // ReqRes /logout returns 200 with empty body
        ResponseValidator.validateStatusCode200(response);

        logPass("Logout endpoint responded with 200");
    }
}
