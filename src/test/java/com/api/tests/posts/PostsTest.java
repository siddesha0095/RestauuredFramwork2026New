package com.api.tests.posts;

import com.api.framework.base.BaseTest;
import com.api.framework.endpoints.PostEndpoints;
import com.api.framework.models.request.PostRequest;
import com.api.framework.models.response.PostResponse;
import com.api.framework.utils.JsonUtils;
import com.api.framework.utils.ResponseValidator;
import com.api.framework.utils.RetryAnalyzer;
import com.api.framework.utils.TestDataGenerator;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * PostsTest - Tests for JSONPlaceholder /posts CRUD + advanced RestAssured features.
 *
 * Covers:
 *  - DataProvider (parameterized tests)
 *  - GPath / JsonPath expressions
 *  - List filtering & extraction
 *  - Nested JSON validation
 *  - PUT / PATCH / DELETE
 *  - Query param filtering
 *  - Comments nested resource
 *  - Soft assertions via ResponseValidator
 */
public class PostsTest extends BaseTest {

    // ==================== GET ALL POSTS ====================

    @Test(description = "GET /posts returns 200 and 100 posts",
          retryAnalyzer = RetryAnalyzer.class)
    public void testGetAllPostsReturns100() {
        logStep("GET /posts");
        Response response = PostEndpoints.getAllPosts();

        ResponseValidator.validateSuccessResponse(response);

        List<Object> posts = response.jsonPath().getList("$");
        assertThat(posts).hasSize(100);

        logPass("All 100 posts returned");
    }

    @Test(description = "GET /posts — every post has id, title, body, userId")
    public void testAllPostsHaveRequiredFields() {
        logStep("GET /posts and verify required fields on all items");
        Response response = PostEndpoints.getAllPosts();

        ResponseValidator.validateStatusCode200(response);

        response.then()
                .body("id",     everyItem(notNullValue()))
                .body("title",  everyItem(notNullValue()))
                .body("body",   everyItem(notNullValue()))
                .body("userId", everyItem(notNullValue()));

        logPass("All posts have required fields");
    }

    @Test(description = "GET /posts — extract all titles using GPath")
    public void testExtractAllTitlesWithGPath() {
        logStep("GET /posts and extract all titles via GPath");
        Response response = PostEndpoints.getAllPosts();

        ResponseValidator.validateStatusCode200(response);

        List<String> titles = response.jsonPath().getList("findAll{it.userId==1}.title");
        assertThat(titles).isNotEmpty();

        logPass("Titles for userId=1 via GPath: count=" + titles.size());
    }

    @Test(description = "GET /posts — filter posts with userId < 3 via GPath")
    public void testGPathFilterPostsByUserId() {
        logStep("GET /posts and GPath filter userId < 3");
        Response response = PostEndpoints.getAllPosts();

        ResponseValidator.validateStatusCode200(response);

        List<Map<String, Object>> filteredPosts =
                response.jsonPath().getList("findAll{it.userId < 3}");

        assertThat(filteredPosts).isNotEmpty();
        filteredPosts.forEach(post -> {
            int userId = (Integer) post.get("userId");
            assertThat(userId).isLessThan(3);
        });

        logPass("GPath filter returned " + filteredPosts.size() + " posts with userId < 3");
    }

    @Test(description = "GET /posts — JsonPath collect unique userIds")
    public void testCollectUniqueUserIds() {
        logStep("GET /posts and collect distinct userIds");
        Response response = PostEndpoints.getAllPosts();

        ResponseValidator.validateStatusCode200(response);

        List<Integer> allUserIds    = response.jsonPath().getList("userId");
        long distinctCount = allUserIds.stream().distinct().count();

        assertThat(distinctCount).isGreaterThan(1);

        logPass("Found " + distinctCount + " distinct userIds across " + allUserIds.size() + " posts");
    }

    // ==================== GET POST BY ID (DataProvider) ====================

    @DataProvider(name = "validPostIds")
    public Object[][] validPostIds() {
        return new Object[][]{
                {1}, {10}, {50}, {100}
        };
    }

    @Test(description = "GET /posts/{id} — parameterized across multiple valid ids",
          dataProvider = "validPostIds")
    public void testGetPostByIdParameterized(int postId) {
        logStep("GET /posts/" + postId);
        Response response = PostEndpoints.getPostById(postId);

        ResponseValidator.validateStatusCode200(response);

        response.then()
                .body("id", equalTo(postId));

        PostResponse post = JsonUtils.fromResponse(response, PostResponse.class);
        assertThat(post.getId()).isEqualTo(postId);
        assertThat(post.getTitle()).isNotBlank();

        logPass("Post id=" + postId + " fetched. title=" + post.getTitle());
    }

    @Test(description = "GET /posts/{id} with invalid id returns 404")
    public void testGetPostByInvalidIdReturns404() {
        logStep("GET /posts/99999 — expect 404");
        Response response = PostEndpoints.getPostById(99999);

        ResponseValidator.validateStatusCode404(response);
        logPass("404 returned for invalid post id");
    }

    // ==================== GET POSTS BY USER ID (QUERY PARAM) ====================

    @Test(description = "GET /posts?userId=1 returns only posts for userId=1")
    public void testGetPostsByUserId() {
        int userId = 1;
        logStep("GET /posts?userId=" + userId);
        Response response = PostEndpoints.getPostsByUserId(userId);

        ResponseValidator.validateStatusCode200(response);

        List<Integer> userIds = response.jsonPath().getList("userId");
        assertThat(userIds).isNotEmpty();
        userIds.forEach(id -> assertThat(id).isEqualTo(userId));

        logPass("All " + userIds.size() + " posts belong to userId=" + userId);
    }

    // ==================== GET COMMENTS FOR POST ====================

    @Test(description = "GET /posts/{id}/comments returns non-empty list")
    public void testGetCommentsForPost() {
        int postId = 1;
        logStep("GET /posts/" + postId + "/comments");
        Response response = PostEndpoints.getCommentsForPost(postId);

        ResponseValidator.validateStatusCode200(response);

        List<Object> comments = response.jsonPath().getList("$");
        assertThat(comments).isNotEmpty();

        logPass("Comments count for postId=" + postId + ": " + comments.size());
    }

    @Test(description = "GET /posts/{id}/comments — every comment has email field")
    public void testCommentsHaveEmailField() {
        logStep("GET /posts/1/comments and verify email fields");
        Response response = PostEndpoints.getCommentsForPost(1);

        ResponseValidator.validateStatusCode200(response);

        response.then()
                .body("email", everyItem(notNullValue()))
                .body("email", everyItem(containsString("@")));

        logPass("All comments have valid email fields");
    }

    @Test(description = "GET /comments?postId={id} returns same data as nested endpoint")
    public void testCommentsViaQueryParamVsNestedPath() {
        int postId = 2;
        logStep("Compare /posts/2/comments vs /comments?postId=2");
        Response nestedResponse = PostEndpoints.getCommentsForPost(postId);
        Response queryResponse  = PostEndpoints.getCommentsByPostIdQueryParam(postId);

        ResponseValidator.validateStatusCode200(nestedResponse);
        ResponseValidator.validateStatusCode200(queryResponse);

        List<Integer> nestedIds = nestedResponse.jsonPath().getList("id");
        List<Integer> queryIds  = queryResponse.jsonPath().getList("id");

        assertThat(nestedIds).containsExactlyInAnyOrderElementsOf(queryIds);

        logPass("Both endpoints return same comment ids: " + nestedIds);
    }

    // ==================== CREATE POST ====================

    @Test(description = "POST /posts with POJO returns 201 with echoed fields")
    public void testCreatePost() {
        PostRequest request = PostRequest.builder()
                .title(TestDataGenerator.getRandomTitle())
                .body(TestDataGenerator.getRandomBody())
                .userId(TestDataGenerator.getRandomUserId())
                .build();

        logStep("POST /posts — title=" + request.getTitle());
        Response response = PostEndpoints.createPost(request);

        ResponseValidator.validateCreatedResponse(response);

        response.then()
                .body("id",     notNullValue())
                .body("title",  equalTo(request.getTitle()))
                .body("body",   equalTo(request.getBody()))
                .body("userId", equalTo(request.getUserId()));

        logPass("Post created. id=" + response.jsonPath().getInt("id"));
    }

    @Test(description = "POST /posts — deserialize response to PostResponse POJO")
    public void testCreatePostDeserializeToPojo() {
        PostRequest request = PostRequest.builder()
                .title("POJO Deserialization Test")
                .body("Testing POJO round-trip")
                .userId(1)
                .build();

        logStep("POST /posts and deserialize to PostResponse");
        Response response = PostEndpoints.createPost(request);

        ResponseValidator.validateStatusCode201(response);

        PostResponse created = JsonUtils.fromResponse(response, PostResponse.class);
        assertThat(created.getId()).isNotNull();
        assertThat(created.getTitle()).isEqualTo(request.getTitle());
        assertThat(created.getUserId()).isEqualTo(request.getUserId());

        logPass("Deserialized PostResponse — id=" + created.getId());
    }

    @Test(description = "POST /posts with Map body returns 201")
    public void testCreatePostWithMap() {
        Map<String, Object> payload = TestDataGenerator.getRandomPostPayload();

        logStep("POST /posts with Map payload");
        Response response = PostEndpoints.createPost(payload);

        ResponseValidator.validateStatusCode201(response);
        ResponseValidator.validateFieldNotNull(response, "id");

        logPass("Post created via Map. id=" + response.jsonPath().getInt("id"));
    }

    // ==================== PUT ====================

    @Test(description = "PUT /posts/{id} — full update returns 200 with all echoed fields")
    public void testUpdatePostWithPut() {
        int postId = 1;
        PostRequest request = PostRequest.builder()
                .title("Updated Title")
                .body("Updated body content for the post")
                .userId(1)
                .build();

        logStep("PUT /posts/" + postId);
        Response response = PostEndpoints.updatePost(postId, request);

        ResponseValidator.validateStatusCode200(response);

        response.then()
                .body("id",     equalTo(postId))
                .body("title",  equalTo(request.getTitle()))
                .body("body",   equalTo(request.getBody()))
                .body("userId", equalTo(request.getUserId()));

        logPass("PUT succeeded for postId=" + postId);
    }

    // ==================== PATCH ====================

    @Test(description = "PATCH /posts/{id} — partial update of title only")
    public void testPatchPost() {
        int postId = 1;
        String newTitle = "Partially Updated Title";

        Map<String, Object> patch = new HashMap<>();
        patch.put("title", newTitle);

        logStep("PATCH /posts/" + postId + " — title=" + newTitle);
        Response response = PostEndpoints.patchPost(postId, patch);

        ResponseValidator.validateStatusCode200(response);

        response.then()
                .body("id",    equalTo(postId))
                .body("title", equalTo(newTitle));

        logPass("PATCH title succeeded for postId=" + postId);
    }

    // ==================== DELETE ====================

    @Test(description = "DELETE /posts/{id} — verify 200 empty body")
    public void testDeletePost() {
        int postId = 1;
        logStep("DELETE /posts/" + postId);
        Response response = PostEndpoints.deletePost(postId);

        // JSONPlaceholder returns 200 for DELETE (not 204)
        ResponseValidator.validateStatusCode200(response);

        logPass("DELETE postId=" + postId + " returned 200");
    }

    // ==================== SOFT ASSERTIONS ====================

    @Test(description = "Validate GET /posts/1 with soft assertions")
    public void testGetPostSoftAssertions() {
        logStep("GET /posts/1 and apply soft assertions via ResponseValidator");
        Response response = PostEndpoints.getPostById(1);

        ResponseValidator.validateWithSoftAssertions(
                response,
                200,
                "application/json",
                5000
        );

        logPass("Soft assertions passed for /posts/1");
    }
}
