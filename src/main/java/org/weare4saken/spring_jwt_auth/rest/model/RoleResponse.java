package org.weare4saken.spring_jwt_auth.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User role response")
public class RoleResponse {

    @Schema(description = "Role name", example = "ROLE_USER")
    @JsonProperty("role")
    private String role;
}