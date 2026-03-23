package com.api.framework.constants;

/**
 * API endpoint constants for all APIs used in the framework.
 * Organized by API provider and resource type.
 */
public final class ApiConstants {

    private ApiConstants() {}

    // ==================== REQRES.IN ENDPOINTS ====================
    public static final class ReqRes {
        private ReqRes() {}

        public static final String BASE_PATH   = "/api";
        public static final String USERS       = "/api/users";
        public static final String USERS_BY_ID = "/api/users/{id}";
        public static final String REGISTER    = "/api/register";
        public static final String LOGIN       = "/api/login";
        public static final String LOGOUT      = "/api/logout";
        public static final String RESOURCES   = "/api/unknown";
        public static final String RESOURCE_BY_ID = "/api/unknown/{id}";
    }

    // ==================== JSON PLACEHOLDER ENDPOINTS ====================
    public static final class JsonPlaceholder {
        private JsonPlaceholder() {}

        public static final String POSTS        = "/posts";
        public static final String POST_BY_ID   = "/posts/{id}";
        public static final String POST_COMMENTS = "/posts/{id}/comments";
        public static final String COMMENTS     = "/comments";
        public static final String ALBUMS       = "/albums";
        public static final String TODOS        = "/todos";
        public static final String USERS        = "/users";
        public static final String PHOTOS       = "/photos";
    }

    // ==================== HTTP STATUS CODES ====================
    public static final class StatusCode {
        private StatusCode() {}

        public static final int OK              = 200;
        public static final int CREATED         = 201;
        public static final int ACCEPTED        = 202;
        public static final int NO_CONTENT      = 204;
        public static final int BAD_REQUEST     = 400;
        public static final int UNAUTHORIZED    = 401;
        public static final int FORBIDDEN       = 403;
        public static final int NOT_FOUND       = 404;
        public static final int METHOD_NOT_ALLOWED = 405;
        public static final int CONFLICT        = 409;
        public static final int UNPROCESSABLE   = 422;
        public static final int TOO_MANY_REQUESTS = 429;
        public static final int INTERNAL_SERVER_ERROR = 500;
        public static final int SERVICE_UNAVAILABLE = 503;
    }

    // ==================== CONTENT TYPES ====================
    public static final class ContentType {
        private ContentType() {}

        public static final String JSON         = "application/json";
        public static final String XML          = "application/xml";
        public static final String FORM_URLENCODED = "application/x-www-form-urlencoded";
        public static final String MULTIPART    = "multipart/form-data";
        public static final String TEXT_PLAIN   = "text/plain";
        public static final String TEXT_HTML    = "text/html";
    }

    // ==================== COMMON HEADERS ====================
    public static final class Headers {
        private Headers() {}

        public static final String CONTENT_TYPE    = "Content-Type";
        public static final String ACCEPT          = "Accept";
        public static final String AUTHORIZATION   = "Authorization";
        public static final String X_API_KEY       = "x-api-key";
        public static final String X_REQUEST_ID    = "x-request-id";
        public static final String X_CORRELATION_ID = "x-correlation-id";
        public static final String CACHE_CONTROL   = "Cache-Control";
        public static final String USER_AGENT      = "User-Agent";
    }

    // ==================== AUTH ====================
    public static final class Auth {
        private Auth() {}

        public static final String BEARER_PREFIX   = "Bearer ";
        public static final String BASIC_PREFIX    = "Basic ";
    }

    // ==================== QUERY PARAMS ====================
    public static final class QueryParams {
        private QueryParams() {}

        public static final String PAGE            = "page";
        public static final String PER_PAGE        = "per_page";
        public static final String DELAY           = "delay";
    }

    // ==================== RESPONSE FIELDS ====================
    public static final class ResponseFields {
        private ResponseFields() {}

        public static final String DATA            = "data";
        public static final String PAGE            = "page";
        public static final String PER_PAGE        = "per_page";
        public static final String TOTAL           = "total";
        public static final String TOTAL_PAGES     = "total_pages";
        public static final String ID              = "id";
        public static final String EMAIL           = "email";
        public static final String FIRST_NAME      = "first_name";
        public static final String LAST_NAME       = "last_name";
        public static final String AVATAR          = "avatar";
        public static final String TOKEN           = "token";
        public static final String ERROR           = "error";
        public static final String SUPPORT         = "support";
    }
}
