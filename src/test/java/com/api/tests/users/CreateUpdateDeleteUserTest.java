package com.api.tests.users;

import com.api.framework.base.BaseTest;
import com.api.framework.endpoints.UserEndpoints;
import com.api.framework.models.request.CreateUserRequest;
import com.api.framework.models.response.CreateUserResponse;
import com.api.framework.utils.JsonUtils;
import com.api.framework.utils.ResponseValidator;
import com.api.framework.utils.RetryAnalyzer;
import com.api.framework.utils.TestDataGenerator;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * CreateUpdateDeleteUserTest - Tests for POST, PUT, PATCH, DELETE /api/users
 *
 * Covers:
 *  - Request body serialization (POJO, Map, raw JSON)
 *  - 201 Created validation
 *  - Response field extraction
 *  - POJO deserialization from create response
 *  - PUT full update
 *  - PATCH partial update
 *  - DELETE — 204 No Content
 *  - Negative scenarios (missing required field)
 */
public class CreateUpdateDeleteUserTest extends BaseTest {

    // ==================== POST — CREATE USER ====================

    @Test(description = "POST /users with POJO body — verify 201 and response fields",
          retryAnalyzer = RetryAnalyzer.class)
    public void testCreateUserWithPojo() {
        String name = TestDataGenerator.getRandomFullName();
        String job  = TestDataGenerator.getRandomJobTitle();

        CreateUserRequest request = CreateUserRequest.builder()
                .name(name)
                .job(job)
                .build();

        logStep("POST /api/users — name=" + name + ", job=" + job);
        Response response = UserEndpoints.createUser(request);

        ResponseValidator.validateCreatedResponse(response);

        response.then()
                .body("id",        notNullValue())
                .body("name",      equalTo(name))
                .body("job",       equalTo(job))
                .body("createdAt", notNullValue());

        logPass("User created. id=" + response.jsonPath().getString("id"));
    }

    @Test(description = "POST /users — deserialize create response to POJO")
    public void testCreateUserDeserializationToPojo() {
        String name = TestDataGenerator.getRandomFullName();
        String job  = TestDataGenerator.getRandomJobTitle();

        CreateUserRequest request = CreateUserRequest.builder()
                .name(name).job(job).build();

        logStep("POST /api/users and deserialize to CreateUserResponse");
        Response response = UserEndpoints.createUser(request);

        ResponseValidator.validateStatusCode201(response);

        CreateUserResponse created = JsonUtils.fromResponse(response, CreateUserResponse.class);

        assertThat(created.getId()).isNotBlank();
        assertThat(created.getName()).isEqualTo(name);
        assertThat(created.getJob()).isEqualTo(job);
        assertThat(created.getCreatedAt()).isNotBlank();

        logPass("Deserialized CreateUserResponse — id=" + created.getId());
    }

    @Test(description = "POST /users with Map body — verify 201")
    public void testCreateUserWithMapBody() {
        Map<String, Object> payload = TestDataGenerator.getRandomUserPayload();

        logStep("POST /api/users with Map payload=" + payload);
        Response response = UserEndpoints.createUser(payload);

        ResponseValidator.validateStatusCode201(response);
        ResponseValidator.validateFieldNotNull(response, "id");
        ResponseValidator.validateFieldValue(response, "name", payload.get("name"));

        logPass("User created via Map payload");
    }

    @Test(description = "POST /users with raw JSON string body — verify 201")
    public void testCreateUserWithRawJson() {
        String rawJson = "{\"name\":\"Raw Json User\",\"job\":\"QA Engineer\"}";

        logStep("POST /api/users with raw JSON: " + rawJson);
        Response response = UserEndpoints.createUserWithRawJson(rawJson);

        ResponseValidator.validateStatusCode201(response);
        response.then()
                .body("name", equalTo("Raw Json User"))
                .body("job",  equalTo("QA Engineer"));

        logPass("User created via raw JSON body");
    }

    @Test(description = "POST /users — verify id is unique for each call")
    public void testCreateUserIdsAreUnique() {
        CreateUserRequest req1 = CreateUserRequest.builder()
                .name(TestDataGenerator.getRandomFullName())
                .job(TestDataGenerator.getRandomJobTitle())
                .build();

        CreateUserRequest req2 = CreateUserRequest.builder()
                .name(TestDataGenerator.getRandomFullName())
                .job(TestDataGenerator.getRandomJobTitle())
                .build();

        logStep("POST /api/users twice and compare generated IDs");
        String id1 = UserEndpoints.createUser(req1).jsonPath().getString("id");
        String id2 = UserEndpoints.createUser(req2).jsonPath().getString("id");

        assertThat(id1).isNotEqualTo(id2);

        logPass("IDs are unique: " + id1 + " != " + id2);
    }

    @Test(description = "POST /users — verify createdAt is a non-blank timestamp")
    public void testCreateUserCreatedAtNotBlank() {
        CreateUserRequest request = CreateUserRequest.builder()
                .name("Timestamp Tester")
                .job("QA")
                .build();

        logStep("POST /api/users and check createdAt field");
        Response response = UserEndpoints.createUser(request);

        ResponseValidator.validateStatusCode201(response);

        String createdAt = response.jsonPath().getString("createdAt");
        assertThat(createdAt).isNotBlank();

        logPass("createdAt timestamp: " + createdAt);
    }

    // ==================== PUT — FULL UPDATE ====================

    @Test(description = "PUT /users/{id} — verify 200 and updatedAt in response")
    public void testUpdateUserWithPut() {
        int userId = 2;
        String newName = TestDataGenerator.getRandomFullName();
        String newJob  = TestDataGenerator.getRandomJobTitle();

        CreateUserRequest request = CreateUserRequest.builder()
                .name(newName).job(newJob).build();

        logStep("PUT /api/users/" + userId + " — name=" + newName + ", job=" + newJob);
        Response response = UserEndpoints.updateUser(userId, request);

        ResponseValidator.validateStatusCode200(response);

        response.then()
                .body("name",      equalTo(newName))
                .body("job",       equalTo(newJob))
                .body("updatedAt", notNullValue());

        logPass("User updated via PUT. updatedAt=" + response.jsonPath().getString("updatedAt"));
    }

    @Test(description = "PUT /users/{id} — response body reflects sent values")
    public void testPutResponseReflectsPayload() {
        int userId = 5;
        String name = "Full Replace Name";
        String job  = "Senior Engineer";

        CreateUserRequest request = CreateUserRequest.builder()
                .name(name).job(job).build();

        logStep("PUT /api/users/" + userId);
        Response response = UserEndpoints.updateUser(userId, request);

        ResponseValidator.validateStatusCode200(response);

        ResponseValidator.validateFieldValue(response, "name", name);
        ResponseValidator.validateFieldValue(response, "job",  job);

        logPass("PUT response correctly reflects the sent payload");
    }

    // ==================== PATCH — PARTIAL UPDATE ====================

    @Test(description = "PATCH /users/{id} — partial update of job field")
    public void testPatchUserJob() {
        int userId = 2;
        String newJob = "Lead Automation Engineer";

        Map<String, Object> patch = new HashMap<>();
        patch.put("job", newJob);

        logStep("PATCH /api/users/" + userId + " — job=" + newJob);
        Response response = UserEndpoints.patchUser(userId, patch);

        ResponseValidator.validateStatusCode200(response);

        response.then()
                .body("job",       equalTo(newJob))
                .body("updatedAt", notNullValue());

        logPass("PATCH succeeded. updatedAt=" + response.jsonPath().getString("updatedAt"));
    }

    @Test(description = "PATCH /users/{id} — partial update of name field")
    public void testPatchUserName() {
        int userId = 3;
        String newName = TestDataGenerator.getRandomFullName();

        Map<String, Object> patch = new HashMap<>();
        patch.put("name", newName);

        logStep("PATCH /api/users/" + userId + " — name=" + newName);
        Response response = UserEndpoints.patchUser(userId, patch);

        ResponseValidator.validateStatusCode200(response);
        ResponseValidator.validateFieldValue(response, "name", newName);

        logPass("PATCH name succeeded");
    }

    // ==================== DELETE ====================

    @Test(description = "DELETE /users/{id} — verify 204 No Content")
    public void testDeleteUserReturns204() {
        int userId = 2;

        logStep("DELETE /api/users/" + userId);
        Response response = UserEndpoints.deleteUser(userId);

        ResponseValidator.validateStatusCode204(response);

        String body = response.getBody().asString();
        assertThat(body).isEmpty();

        logPass("DELETE returned 204 with empty body");
    }

    @Test(description = "DELETE /users/{id} — multiple different ids return 204")
    public void testDeleteMultipleUsersReturn204() {
        int[] userIds = {1, 3, 5};

        for (int userId : userIds) {
            logStep("DELETE /api/users/" + userId);
            Response response = UserEndpoints.deleteUser(userId);
            ResponseValidator.validateStatusCode204(response);
        }

        logPass("All DELETE calls returned 204");
    }

    // ==================== NEGATIVE SCENARIOS ====================

    @Test(description = "POST /users with empty body — verify server accepts or rejects gracefully")
    public void testCreateUserWithEmptyBody() {
        logStep("POST /api/users with empty payload {}");
        Response response = UserEndpoints.createUser(new HashMap<>());

        // ReqRes accepts empty payloads — it returns 201 with null/empty fields
        int statusCode = response.statusCode();
        assertThat(statusCode).isIn(201, 400);

        logPass("Server responded with " + statusCode + " for empty body");
    }
}
