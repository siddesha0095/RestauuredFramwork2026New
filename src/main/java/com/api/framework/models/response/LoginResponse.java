package com.api.framework.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LoginResponse - Deserialization POJO for login/register endpoints.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResponse {

    @JsonProperty("token")
    private String token;

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("error")
    private String error;
}
