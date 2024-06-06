package org.weare4saken.spring_jwt_auth.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "JWT authentication response")
public class AccessTokenResponse {

    @Schema(description = "Access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJNaWtlIiwicm9sZ...")
    @JsonProperty("token")
    private final String token;
}
