package com.api.framework.models.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CreateUserRequest - POJO for creating a user via ReqRes API.
 * Uses Lombok for boilerplate and Jackson for JSON mapping.
 * Null fields are excluded from serialization.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateUserRequest {

    @JsonProperty("name")
    private String name;

    @JsonProperty("job")
    private String job;
}
