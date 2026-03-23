package com.api.framework.endpoints;

import com.api.framework.constants.ApiConstants;
import com.api.framework.models.request.PostRequest;
import com.api.framework.utils.SpecBuilder;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * PostEndpoints - API client for JSONPlaceholder /posts endpoints.
 * Demonstrates full CRUD + filtering via query params.
 */
public class PostEndpoints {

    private static final Logger log = LogManager.getLogger(PostEndpoints.class);

    private PostEndpoints() {}

    // ==================== GET ====================

    public static Response getAllPosts() {
        log.info("GET all posts");
        return given()
                .spec(SpecBuilder.getJsonPlaceholderSpec())
                .when()
                .get(ApiConstants.JsonPlaceholder.POSTS);
    }

    public static Response getPostById(int postId) {
        log.info("GET post by id={}", postId);
        return given()
                .spec(SpecBuilder.getJsonPlaceholderSpec())
                .pathParam("id", postId)
                .when()
                .get(ApiConstants.JsonPlaceholder.POST_BY_ID);
    }

    public static Response getPostsByUserId(int userId) {
        log.info("GET posts for userId={}", userId);
        return given()
                .spec(SpecBuilder.getJsonPlaceholderSpec())
                .queryParam("userId", userId)
                .when()
                .get(ApiConstants.JsonPlaceholder.POSTS);
    }

    public static Response getCommentsForPost(int postId) {
        log.info("GET comments for postId={}", postId);
        return given()
                .spec(SpecBuilder.getJsonPlaceholderSpec())
                .pathParam("id", postId)
                .when()
                .get(ApiConstants.JsonPlaceholder.POST_COMMENTS);
    }

    public static Response getCommentsByPostIdQueryParam(int postId) {
        log.info("GET comments via query param postId={}", postId);
        return given()
                .spec(SpecBuilder.getJsonPlaceholderSpec())
                .queryParam("postId", postId)
                .when()
                .get(ApiConstants.JsonPlaceholder.COMMENTS);
    }

    // ==================== POST ====================

    public static Response createPost(PostRequest request) {
        log.info("POST create post — title={}", request.getTitle());
        return given()
                .spec(SpecBuilder.getJsonPlaceholderSpec())
                .body(request)
                .when()
                .post(ApiConstants.JsonPlaceholder.POSTS);
    }

    public static Response createPost(Map<String, Object> payload) {
        log.info("POST create post — payload={}", payload);
        return given()
                .spec(SpecBuilder.getJsonPlaceholderSpec())
                .body(payload)
                .when()
                .post(ApiConstants.JsonPlaceholder.POSTS);
    }

    // ==================== PUT ====================

    public static Response updatePost(int postId, PostRequest request) {
        log.info("PUT update post id={}", postId);
        return given()
                .spec(SpecBuilder.getJsonPlaceholderSpec())
                .pathParam("id", postId)
                .body(request)
                .when()
                .put(ApiConstants.JsonPlaceholder.POST_BY_ID);
    }

    // ==================== PATCH ====================

    public static Response patchPost(int postId, Map<String, Object> payload) {
        log.info("PATCH post id={} — payload={}", postId, payload);
        return given()
                .spec(SpecBuilder.getJsonPlaceholderSpec())
                .pathParam("id", postId)
                .body(payload)
                .when()
                .patch(ApiConstants.JsonPlaceholder.POST_BY_ID);
    }

    // ==================== DELETE ====================

    public static Response deletePost(int postId) {
        log.info("DELETE post id={}", postId);
        return given()
                .spec(SpecBuilder.getJsonPlaceholderSpec())
                .pathParam("id", postId)
                .when()
                .delete(ApiConstants.JsonPlaceholder.POST_BY_ID);
    }
}
