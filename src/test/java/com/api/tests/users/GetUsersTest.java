package com.api.tests.users;

import com.api.framework.base.BaseTest;
import com.api.framework.constants.ApiConstants;
import com.api.framework.endpoints.UserEndpoints;
import com.api.framework.models.response.UserListResponse;
import com.api.framework.models.response.UserResponse;
import com.api.framework.utils.JsonUtils;
import com.api.framework.utils.ResponseValidator;
import com.api.framework.utils.RetryAnalyzer;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * GetUsersTest - Tests for GET /api/users and GET /api/users/{id}
 *
 * Covers:
 *  - Status code validation
 *  - JSON body validation (Hamcrest + AssertJ)
 *  - Deserialization to POJO
 *  - Pagination query params
 *  - Path params
 *  - Response time assertions
 *  - Content-Type validation
 *  - Negative / not-found scenarios
 */
public class GetUsersTest extends BaseTest {

    // ==================== GET ALL USERS ====================

    @Test(description = "Verify GET /users returns 200 with valid body",
          retryAnalyzer = RetryAnalyzer.class)
    public void testGetUsersReturns200() {
        logStep("Sending GET /api/users?page=1");
        Response response = UserEndpoints.getUsers(1);

        logStep("Validating status code, content-type and response time");
        ResponseValidator.validateStatusCode200(response);
        ResponseValidator.validateJsonContentType(response);
        ResponseValidator.validateResponseTimeUnder5Seconds(response);

        logPass("GET /users returned 200 with valid headers");
    }

    @Test(description = "Verify pagination fields exist in response")
    public void testGetUsersPaginationFields() {
        logStep("GET /api/users?page=1");
        Response response = UserEndpoints.getUsers(1);

        ResponseValidator.validateStatusCode200(response);

        logStep("Asserting pagination fields using Hamcrest");
        response.then()
                .body(ApiConstants.ResponseFields.PAGE,       equalTo(1))
                .body(ApiConstants.ResponseFields.PER_PAGE,   notNullValue())
                .body(ApiConstants.ResponseFields.TOTAL,      greaterThan(0))
                .body(ApiConstants.ResponseFields.TOTAL_PAGES, greaterThan(0));

        logPass("Pagination fields validated");
    }

    @Test(description = "Verify user data list is not empty")
    public void testGetUsersDataListNotEmpty() {
        logStep("GET /api/users?page=1");
        Response response = UserEndpoints.getUsers(1);

        ResponseValidator.validateStatusCode200(response);
        ResponseValidator.validateListNotEmpty(response, "data");

        List<Object> data = response.jsonPath().getList("data");
        assertThat(data).hasSizeGreaterThan(0);

        logPass("User data list is non-empty. Count=" + data.size());
    }

    @Test(description = "Verify GET /users with per_page param limits result size")
    public void testGetUsersPerPageParam() {
        int perPage = 3;
        logStep("GET /api/users?page=1&per_page=" + perPage);
        Response response = UserEndpoints.getUsersWithPerPage(1, perPage);

        ResponseValidator.validateStatusCode200(response);

        List<Object> data = response.jsonPath().getList("data");
        assertThat(data).hasSizeLessThanOrEqualTo(perPage);

        logPass("per_page param respected. Returned " + data.size() + " records");
    }

    @Test(description = "Verify user data fields — id, email, first_name, last_name, avatar")
    public void testGetUsersDataFieldsNotNull() {
        logStep("GET /api/users?page=1");
        Response response = UserEndpoints.getUsers(1);

        ResponseValidator.validateStatusCode200(response);

        response.then()
                .body("data[0].id",         notNullValue())
                .body("data[0].email",       notNullValue())
                .body("data[0].first_name",  notNullValue())
                .body("data[0].last_name",   notNullValue())
                .body("data[0].avatar",      notNullValue());

        logPass("All user data fields are present in the first item");
    }

    @Test(description = "Verify email format in user list")
    public void testGetUsersEmailFormat() {
        logStep("GET /api/users?page=1 and validate email format");
        Response response = UserEndpoints.getUsers(1);

        ResponseValidator.validateStatusCode200(response);

        List<String> emails = response.jsonPath().getList("data.email");
        emails.forEach(email ->
                assertThat(email).as("Email should contain @").contains("@")
        );

        logPass("All emails contain @. Count=" + emails.size());
    }

    @Test(description = "Deserialize user list response to POJO and validate")
    public void testGetUsersDeserializationToPojo() {
        logStep("GET /api/users and deserialize to UserListResponse POJO");
        Response response = UserEndpoints.getUsers(1);

        ResponseValidator.validateStatusCode200(response);

        UserListResponse userList = JsonUtils.fromResponse(response, UserListResponse.class);

        assertThat(userList).isNotNull();
        assertThat(userList.getPage()).isEqualTo(1);
        assertThat(userList.getData()).isNotNull().isNotEmpty();
        assertThat(userList.getTotal()).isGreaterThan(0);

        UserListResponse.UserData firstUser = userList.getData().get(0);
        assertThat(firstUser.getId()).isNotNull();
        assertThat(firstUser.getEmail()).isNotBlank();
        assertThat(firstUser.getFirstName()).isNotBlank();
        assertThat(firstUser.getLastName()).isNotBlank();

        logPass("POJO deserialization succeeded. First user: "
                + firstUser.getFirstName() + " " + firstUser.getLastName());
    }

    @Test(description = "Verify page 2 returns different data than page 1")
    public void testGetUsersPage2ReturnsDifferentData() {
        logStep("GET /api/users?page=1 and page=2, compare ids");
        Response page1 = UserEndpoints.getUsers(1);
        Response page2 = UserEndpoints.getUsers(2);

        ResponseValidator.validateStatusCode200(page1);
        ResponseValidator.validateStatusCode200(page2);

        List<Integer> ids1 = page1.jsonPath().getList("data.id");
        List<Integer> ids2 = page2.jsonPath().getList("data.id");

        assertThat(ids1).doesNotContainAnyElementsOf(ids2);

        logPass("Pages 1 and 2 contain distinct user IDs");
    }

    @Test(description = "Verify support object is present in list response")
    public void testGetUsersSupportObject() {
        logStep("GET /api/users?page=1 and check support field");
        Response response = UserEndpoints.getUsers(1);

        ResponseValidator.validateStatusCode200(response);

        response.then()
                .body("support.url",  notNullValue())
                .body("support.text", notNullValue());

        logPass("Support object is present with url and text");
    }

    // ==================== GET USER BY ID ====================

    @Test(description = "Verify GET /users/{id} returns correct user")
    public void testGetUserByIdReturns200() {
        int userId = 2;
        logStep("GET /api/users/" + userId);
        Response response = UserEndpoints.getUserById(userId);

        ResponseValidator.validateStatusCode200(response);

        response.then()
                .body("data.id",    equalTo(userId))
                .body("data.email", notNullValue());

        logPass("User id=" + userId + " retrieved successfully");
    }

    @Test(description = "Deserialize single user GET response to POJO")
    public void testGetUserByIdDeserializationToPojo() {
        int userId = 1;
        logStep("GET /api/users/" + userId + " and deserialize to UserResponse POJO");
        Response response = UserEndpoints.getUserById(userId);

        ResponseValidator.validateStatusCode200(response);

        UserResponse userResponse = JsonUtils.fromResponse(response, UserResponse.class);

        assertThat(userResponse).isNotNull();
        assertThat(userResponse.getData()).isNotNull();
        assertThat(userResponse.getData().getId()).isEqualTo(userId);
        assertThat(userResponse.getData().getEmail()).isNotBlank();
        assertThat(userResponse.getData().getFirstName()).isNotBlank();
        assertThat(userResponse.getData().getLastName()).isNotBlank();
        assertThat(userResponse.getData().getAvatar()).isNotBlank();

        logPass("POJO: " + userResponse.getData().getFirstName()
                + " " + userResponse.getData().getLastName()
                + " [" + userResponse.getData().getEmail() + "]");
    }

    @Test(description = "Verify GET /users/{id} with invalid id returns 404")
    public void testGetUserByInvalidIdReturns404() {
        int invalidId = 9999;
        logStep("GET /api/users/" + invalidId + " — expect 404");
        Response response = UserEndpoints.getUserById(invalidId);

        ResponseValidator.validateStatusCode404(response);

        // Body should be empty JSON {}
        String body = response.getBody().asString();
        assertThat(body).isIn("{}", "");

        logPass("404 returned for non-existent user id=" + invalidId);
    }

    @Test(description = "Verify avatar URL in response is a valid https URL")
    public void testGetUserAvatarIsHttpsUrl() {
        logStep("GET /api/users/1 and validate avatar URL");
        Response response = UserEndpoints.getUserById(1);

        ResponseValidator.validateStatusCode200(response);

        String avatar = response.jsonPath().getString("data.avatar");
        assertThat(avatar)
                .isNotBlank()
                .startsWith("https://");

        logPass("Avatar URL is valid HTTPS: " + avatar);
    }

    @Test(description = "Validate response time under 5 seconds for GET user by id")
    public void testGetUserResponseTime() {
        logStep("GET /api/users/2 and validate response time");
        Response response = UserEndpoints.getUserById(2);

        ResponseValidator.validateStatusCode200(response);
        ResponseValidator.validateResponseTime(response, 5000);

        logPass("Response time within threshold");
    }

    @Test(description = "Verify all users across page 1 have valid ids")
    public void testAllUserIdsArePositive() {
        logStep("GET /api/users?page=1 and verify all ids > 0");
        Response response = UserEndpoints.getUsers(1);

        ResponseValidator.validateStatusCode200(response);

        List<Integer> ids = response.jsonPath().getList("data.id");
        ids.forEach(id -> assertThat(id).isGreaterThan(0));

        logPass("All user IDs are positive. Count=" + ids.size());
    }
}
