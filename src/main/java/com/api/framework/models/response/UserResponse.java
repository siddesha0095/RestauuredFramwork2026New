package com.api.framework.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * UserResponse - Deserialization POJOs for ReqRes user endpoints.
 * Uses @JsonIgnoreProperties to tolerate extra fields in the response.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse {

    @JsonProperty("data")
    private UserData data;

    @JsonProperty("support")
    private Support support;

    // ==================== NESTED CLASSES ====================

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserData {

        @JsonProperty("id")
        private Integer id;

        @JsonProperty("email")
        private String email;

        @JsonProperty("first_name")
        private String firstName;

        @JsonProperty("last_name")
        private String lastName;

        @JsonProperty("avatar")
        private String avatar;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Support {

        @JsonProperty("url")
        private String url;

        @JsonProperty("text")
        private String text;
    }
}
