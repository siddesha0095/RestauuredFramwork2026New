package com.api.framework.endpoints;

import com.api.framework.constants.ApiConstants;
import com.api.framework.models.request.LoginRequest;
import com.api.framework.utils.SpecBuilder;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * AuthEndpoints - API client for ReqRes authentication endpoints.
 * Covers login, register, and logout flows.
 */
public class AuthEndpoints {

    private static final Logger log = LogManager.getLogger(AuthEndpoints.class);

    private AuthEndpoints() {}

    /**
     * POST /api/login — with POJO
     */
    public static Response login(LoginRequest request) {
        log.info("POST /api/login — email={}", request.getEmail());
        return given()
                .spec(SpecBuilder.getReqResSpec())
                .body(request)
                .when()
                .post(ApiConstants.ReqRes.LOGIN);
    }

    /**
     * POST /api/login — with Map payload
     */
    public static Response login(Map<String, String> payload) {
        log.info("POST /api/login — payload={}", payload);
        return given()
                .spec(SpecBuilder.getReqResSpec())
                .body(payload)
                .when()
                .post(ApiConstants.ReqRes.LOGIN);
    }

    /**
     * POST /api/register — with POJO
     */
    public static Response register(LoginRequest request) {
        log.info("POST /api/register — email={}", request.getEmail());
        return given()
                .spec(SpecBuilder.getReqResSpec())
                .body(request)
                .when()
                .post(ApiConstants.ReqRes.REGISTER);
    }

    /**
     * POST /api/register — with Map payload
     */
    public static Response register(Map<String, String> payload) {
        log.info("POST /api/register — payload={}", payload);
        return given()
                .spec(SpecBuilder.getReqResSpec())
                .body(payload)
                .when()
                .post(ApiConstants.ReqRes.REGISTER);
    }

    /**
     * POST /api/logout
     */
    public static Response logout() {
        log.info("POST /api/logout");
        return given()
                .spec(SpecBuilder.getReqResSpec())
                .when()
                .post(ApiConstants.ReqRes.LOGOUT);
    }

    /**
     * POST /api/login with Bearer token in header (demonstrate token-based auth)
     */
    public static Response loginWithToken(String token) {
        log.info("POST /api/login — with Bearer token");
        return given()
                .spec(SpecBuilder.getReqResSpec())
                .header(ApiConstants.Headers.AUTHORIZATION,
                        ApiConstants.Auth.BEARER_PREFIX + token)
                .when()
                .post(ApiConstants.ReqRes.LOGIN);
    }
}
