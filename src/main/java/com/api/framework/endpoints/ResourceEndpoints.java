package com.api.framework.endpoints;

import com.api.framework.constants.ApiConstants;
import com.api.framework.utils.SpecBuilder;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.restassured.RestAssured.given;

/**
 * ResourceEndpoints - API client for ReqRes /unknown (resources) endpoints.
 */
public class ResourceEndpoints {

    private static final Logger log = LogManager.getLogger(ResourceEndpoints.class);

    private ResourceEndpoints() {}

    public static Response getResources() {
        log.info("GET all resources");
        return given()
                .spec(SpecBuilder.getReqResSpec())
                .when()
                .get(ApiConstants.ReqRes.RESOURCES);
    }

    public static Response getResourceById(int resourceId) {
        log.info("GET resource by id={}", resourceId);
        return given()
                .spec(SpecBuilder.getReqResSpec())
                .pathParam("id", resourceId)
                .when()
                .get(ApiConstants.ReqRes.RESOURCE_BY_ID);
    }

    public static Response getResourcesOnPage(int page) {
        log.info("GET resources on page={}", page);
        return given()
                .spec(SpecBuilder.getReqResSpec())
                .queryParam(ApiConstants.QueryParams.PAGE, page)
                .when()
                .get(ApiConstants.ReqRes.RESOURCES);
    }
}
