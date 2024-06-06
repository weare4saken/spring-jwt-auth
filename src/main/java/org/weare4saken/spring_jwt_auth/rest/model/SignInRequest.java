package org.weare4saken.spring_jwt_auth.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "User authentication request")
public class SignInRequest {

    @Schema(description = "User name", requiredMode = Schema.RequiredMode.REQUIRED, example = "user")
    @Size(min = 3, max = 30, message = "User name must have a length between 3 and 30")
    @NotBlank(message = "User name must not be empty")
    @JsonProperty(value = "username", required = true)
    private String username;

    @Schema(description = "Password", requiredMode = Schema.RequiredMode.REQUIRED, example = "pwd12345")
    @Size(min = 5, max = 250, message = "Password must have a length between 5 and 250")
    @NotBlank(message = "Password must not be empty")
    @JsonProperty(value = "password", required = true)
    private String password;
}
