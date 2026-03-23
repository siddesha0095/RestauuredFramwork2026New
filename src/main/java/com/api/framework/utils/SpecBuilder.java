package com.api.framework.utils;

import com.api.framework.config.ConfigManager;
import com.api.framework.config.FrameworkConfig;
import com.api.framework.constants.ApiConstants;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * SpecBuilder - Centralized factory for creating RestAssured RequestSpecifications.
 * Provides pre-configured specs for different API environments and auth scenarios.
 */
public class SpecBuilder {

    private static final Logger log = LogManager.getLogger(SpecBuilder.class);
    private static final FrameworkConfig config = ConfigManager.getConfig();

    private SpecBuilder() {}

    /**
     * Base specification for ReqRes API (with API key header)
     */
    public static RequestSpecification getReqResSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(config.reqresBaseUrl())
                .setContentType(ContentType.JSON)
                .addHeader(ApiConstants.Headers.ACCEPT, ApiConstants.ContentType.JSON)
                .addHeader(ApiConstants.Headers.X_API_KEY, config.reqresApiKey())
                .addFilter(new AllureRestAssured())
                .addFilter(getRequestLoggingFilter())
                .addFilter(getResponseLoggingFilter())
                .build();
    }

    /**
     * Base specification for JSONPlaceholder API
     */
    public static RequestSpecification getJsonPlaceholderSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(config.jsonPlaceholderBaseUrl())
                .setContentType(ContentType.JSON)
                .addHeader(ApiConstants.Headers.ACCEPT, ApiConstants.ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .addFilter(getRequestLoggingFilter())
                .addFilter(getResponseLoggingFilter())
                .build();
    }

    /**
     * Specification with Bearer token authentication
     */
    public static RequestSpecification getAuthSpec(String baseUrl, String token) {
        return new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .addHeader(ApiConstants.Headers.AUTHORIZATION,
                        ApiConstants.Auth.BEARER_PREFIX + token)
                .addFilter(new AllureRestAssured())
                .addFilter(getRequestLoggingFilter())
                .addFilter(getResponseLoggingFilter())
                .build();
    }

    /**
     * Specification with Basic authentication
     */
    public static RequestSpecification getBasicAuthSpec(String baseUrl,
                                                         String username,
                                                         String password) {
        return new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .setAuth(io.restassured.RestAssured.basic(username, password))
                .addFilter(new AllureRestAssured())
                .addFilter(getRequestLoggingFilter())
                .addFilter(getResponseLoggingFilter())
                .build();
    }

    /**
     * Specification with custom headers map
     */
    public static RequestSpecification getSpecWithHeaders(String baseUrl,
                                                           Map<String, String> headers) {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .addFilter(getRequestLoggingFilter())
                .addFilter(getResponseLoggingFilter());

        headers.forEach(builder::addHeader);
        return builder.build();
    }

    /**
     * Specification with relaxed HTTPS validation (useful for self-signed certs)
     */
    public static RequestSpecification getRelaxedHttpsSpec(String baseUrl) {
        return new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .setRelaxedHTTPSValidation()
                .addFilter(new AllureRestAssured())
                .addFilter(getRequestLoggingFilter())
                .addFilter(getResponseLoggingFilter())
                .build();
    }

    /**
     * Specification with proxy settings
     */
    public static RequestSpecification getProxySpec(String baseUrl) {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .addFilter(getRequestLoggingFilter())
                .addFilter(getResponseLoggingFilter());

        if (config.proxyEnabled()) {
            builder.setProxy(config.proxyHost(), config.proxyPort());
            log.info("Proxy enabled: {}:{}", config.proxyHost(), config.proxyPort());
        }

        return builder.build();
    }

    /**
     * Minimal specification — no logging, no extra headers
     */
    public static RequestSpecification getMinimalSpec(String baseUrl) {
        return new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .build();
    }

    // ==================== PRIVATE HELPERS ====================

    private static RequestLoggingFilter getRequestLoggingFilter() {
        if (config.enableLogging()) {
            return new RequestLoggingFilter(LogDetail.ALL);
        }
        return new RequestLoggingFilter(LogDetail.METHOD);
    }

    private static ResponseLoggingFilter getResponseLoggingFilter() {
        if (config.enableLogging()) {
            return new ResponseLoggingFilter(LogDetail.ALL);
        }
        return new ResponseLoggingFilter(LogDetail.STATUS);
    }

    /**
     * Create a logging filter that writes to a file
     */
    public static RequestLoggingFilter getFileRequestLoggingFilter(String filePath) {
        try {
            PrintStream fileOut = new PrintStream(new FileOutputStream(new File(filePath), true));
            return new RequestLoggingFilter(LogDetail.ALL, fileOut);
        } catch (Exception e) {
            log.warn("Could not create file logging filter, falling back to console", e);
            return new RequestLoggingFilter(LogDetail.ALL);
        }
    }
}
