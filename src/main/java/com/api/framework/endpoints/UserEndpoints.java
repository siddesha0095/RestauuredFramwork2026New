package com.api.framework.endpoints;

import com.api.framework.constants.ApiConstants;
import com.api.framework.models.request.CreateUserRequest;
import com.api.framework.utils.SpecBuilder;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * UserEndpoints - API client for all ReqRes /users endpoints.
 * Each method wraps a single REST call, returning the raw Response.
 * Tests then assert on the response — keeping endpoint logic separate from assertions.
 */
public class UserEndpoints {

    private static final Logger log = LogManager.getLogger(UserEndpoints.class);

    private UserEndpoints() {}

    // ==================== GET ====================

    /**
     * GET /api/users?page={page}
     */
    public static Response getUsers(int page) {
        log.info("GET users list — page={}", page);
        return given()
                .spec(SpecBuilder.getReqResSpec())
                .queryParam(ApiConstants.QueryParams.PAGE, page)
                .when()
                .get(ApiConstants.ReqRes.USERS);
    }

    /**
     * GET /api/users?page={page}&per_page={perPage}
     */
    public static Response getUsersWithPerPage(int page, int perPage) {
        log.info("GET users list — page={}, per_page={}", page, perPage);
        return given()
                .spec(SpecBuilder.getReqResSpec())
                .queryParam(ApiConstants.QueryParams.PAGE, page)
                .queryParam(ApiConstants.QueryParams.PER_PAGE, perPage)
                .when()
                .get(ApiConstants.ReqRes.USERS);
    }

    /**
     * GET /api/users/{id}
     */
    public static Response getUserById(int userId) {
        log.info("GET user by id={}", userId);
        return given()
                .spec(SpecBuilder.getReqResSpec())
                .pathParam("id", userId)
                .when()
                .get(ApiConstants.ReqRes.USERS_BY_ID);
    }

    /**
     * GET /api/users/{id} with simulated delay
     */
    public static Response getUserWithDelay(int userId, int delaySeconds) {
        log.info("GET user id={} with delay={}s", userId, delaySeconds);
        return given()
                .spec(SpecBuilder.getReqResSpec())
                .pathParam("id", userId)
                .queryParam(ApiConstants.QueryParams.DELAY, delaySeconds)
                .when()
                .get(ApiConstants.ReqRes.USERS_BY_ID);
    }

    // ==================== POST ====================

    /**
     * POST /api/users — with POJO body (serialized by RestAssured + Jackson)
     */
    public static Response createUser(CreateUserRequest request) {
        log.info("POST create user — name={}, job={}", request.getName(), request.getJob());
        return given()
                .spec(SpecBuilder.getReqResSpec())
                .body(request)
                .when()
                .post(ApiConstants.ReqRes.USERS);
    }

    /**
     * POST /api/users — with raw Map body
     */
    public static Response createUser(Map<String, Object> payload) {
        log.info("POST create user — payload={}", payload);
        return given()
                .spec(SpecBuilder.getReqResSpec())
                .body(payload)
                .when()
                .post(ApiConstants.ReqRes.USERS);
    }

    /**
     * POST /api/users — with raw JSON string body
     */
    public static Response createUserWithRawJson(String jsonBody) {
        log.info("POST create user with raw JSON");
        return given()
                .spec(SpecBuilder.getReqResSpec())
                .body(jsonBody)
                .when()
                .post(ApiConstants.ReqRes.USERS);
    }

    // ==================== PUT ====================

    /**
     * PUT /api/users/{id} — full update
     */
    public static Response updateUser(int userId, CreateUserRequest request) {
        log.info("PUT update user id={}", userId);
        return given()
                .spec(SpecBuilder.getReqResSpec())
                .pathParam("id", userId)
                .body(request)
                .when()
                .put(ApiConstants.ReqRes.USERS_BY_ID);
    }

    // ==================== PATCH ====================

    /**
     * PATCH /api/users/{id} — partial update
     */
    public static Response patchUser(int userId, Map<String, Object> payload) {
        log.info("PATCH user id={} — payload={}", userId, payload);
        return given()
                .spec(SpecBuilder.getReqResSpec())
                .pathParam("id", userId)
                .body(payload)
                .when()
                .patch(ApiConstants.ReqRes.USERS_BY_ID);
    }

    // ==================== DELETE ====================

    /**
     * DELETE /api/users/{id}
     */
    public static Response deleteUser(int userId) {
        log.info("DELETE user id={}", userId);
        return given()
                .spec(SpecBuilder.getReqResSpec())
                .pathParam("id", userId)
                .when()
                .delete(ApiConstants.ReqRes.USERS_BY_ID);
    }

    // ==================== WITH CUSTOM HEADERS ====================

    /**
     * GET /api/users with additional custom headers
     */
    public static Response getUsersWithCustomHeaders(int page, Map<String, String> headers) {
        log.info("GET users with custom headers — page={}, headers={}", page, headers);
        var requestSpec = given().spec(SpecBuilder.getReqResSpec());
        headers.forEach(requestSpec::header);
        return requestSpec
                .queryParam(ApiConstants.QueryParams.PAGE, page)
                .when()
                .get(ApiConstants.ReqRes.USERS);
    }
}
