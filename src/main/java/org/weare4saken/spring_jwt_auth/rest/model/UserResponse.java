package org.weare4saken.spring_jwt_auth.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.weare4saken.spring_jwt_auth.entity.RoleType;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User information response")
public class UserResponse {
    @Schema(description = "User name", example = "Mike")
    @JsonProperty("username")
    private String username;

    @Schema(description = "User email", example = "mike@gmail.com")
    @JsonProperty("email")
    private String email;

    @Schema(description = "User roles", example = "[\"ROLE_USER\", \"ROLE_ADMIN\"]")
    @JsonProperty("roles")
    private List<RoleType> roles;
}
