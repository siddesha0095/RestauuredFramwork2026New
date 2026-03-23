package com.api.tests.users;

import com.api.framework.base.BaseTest;
import com.api.framework.endpoints.ResourceEndpoints;
import com.api.framework.utils.ResponseValidator;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * ResourcesTest - Tests for ReqRes /api/unknown (resources) endpoint.
 *
 * Covers:
 *  - GET list of resources
 *  - GET resource by ID
 *  - Pagination
 *  - Not found
 *  - Field structure validation
 */
public class ResourcesTest extends BaseTest {

    @Test(description = "GET /api/unknown returns 200 with resource list")
    public void testGetResourcesReturns200() {
        logStep("GET /api/unknown");
        Response response = ResourceEndpoints.getResources();

        ResponseValidator.validateSuccessResponse(response);

        List<Object> data = response.jsonPath().getList("data");
        assertThat(data).isNotEmpty();

        logPass("Resources list returned. Count=" + data.size());
    }

    @Test(description = "GET /api/unknown — each resource has id, name, year, color, pantone_value")
    public void testResourceFieldsExist() {
        logStep("GET /api/unknown and verify resource field structure");
        Response response = ResourceEndpoints.getResources();

        ResponseValidator.validateStatusCode200(response);

        response.then()
                .body("data[0].id",            notNullValue())
                .body("data[0].name",           notNullValue())
                .body("data[0].year",           notNullValue())
                .body("data[0].color",          notNullValue())
                .body("data[0].pantone_value",  notNullValue());

        logPass("All resource fields present");
    }

    @Test(description = "GET /api/unknown — color fields start with #")
    public void testResourceColorFormat() {
        logStep("GET /api/unknown and verify color starts with #");
        Response response = ResourceEndpoints.getResources();

        ResponseValidator.validateStatusCode200(response);

        List<String> colors = response.jsonPath().getList("data.color");
        colors.forEach(color -> assertThat(color).startsWith("#"));

        logPass("All color values start with #");
    }

    @Test(description = "GET /api/unknown/{id} returns correct resource")
    public void testGetResourceById() {
        int resourceId = 2;
        logStep("GET /api/unknown/" + resourceId);
        Response response = ResourceEndpoints.getResourceById(resourceId);

        ResponseValidator.validateStatusCode200(response);

        response.then()
                .body("data.id", equalTo(resourceId));

        logPass("Resource id=" + resourceId + " fetched: "
                + response.jsonPath().getString("data.name"));
    }

    @Test(description = "GET /api/unknown/{id} with invalid id returns 404")
    public void testGetInvalidResourceReturns404() {
        logStep("GET /api/unknown/9999 — expect 404");
        Response response = ResourceEndpoints.getResourceById(9999);

        ResponseValidator.validateStatusCode404(response);
        logPass("404 returned for invalid resource id");
    }

    @Test(description = "GET /api/unknown — pagination fields present")
    public void testResourcesPaginationFields() {
        logStep("GET /api/unknown and verify pagination fields");
        Response response = ResourceEndpoints.getResources();

        ResponseValidator.validateStatusCode200(response);

        response.then()
                .body("page",        notNullValue())
                .body("per_page",    notNullValue())
                .body("total",       greaterThan(0))
                .body("total_pages", greaterThan(0));

        logPass("Pagination fields validated");
    }
}
