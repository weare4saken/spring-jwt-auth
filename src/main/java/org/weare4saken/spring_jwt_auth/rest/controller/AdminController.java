package org.weare4saken.spring_jwt_auth.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.weare4saken.spring_jwt_auth.entity.RoleType;
import org.weare4saken.spring_jwt_auth.rest.model.RoleResponse;
import org.weare4saken.spring_jwt_auth.rest.model.UserResponse;
import org.weare4saken.spring_jwt_auth.service.RoleService;
import org.weare4saken.spring_jwt_auth.service.UserService;
import org.weare4saken.spring_jwt_auth.entity.Role;

import java.util.List;
import java.util.Map;

@Tag(name = "Admin")
@RestController
@RequestMapping(value = "/api/v1/admin", produces = MediaType.APPLICATION_JSON_VALUE)
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final RoleService roleService;
    private final UserService userService;

    @GetMapping("/users")
    @Operation(
            summary = "Retrieve a list of all users",
            description = "Must have an ADMIN role to have access to this endpoint",
            responses = @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            schemaProperties = @SchemaProperty(
                                    name = "users",
                                    array = @ArraySchema(
                                            schema = @Schema(
                                                    implementation = UserResponse.class)
                                    )
                            )
                    )
            )
    )
    public Map<String, List<UserResponse>> getAllUsers() {
        List<UserResponse> users = this.userService.getAllUsers().stream().map(user -> {
                    List<RoleType> roleTypes = user.getRoles().stream()
                            .map(Role::getType)
                            .toList();
                    return new UserResponse(user.getUsername(), user.getEmail(), roleTypes);
                }
        ).toList();
        return Map.of("users", users);
    }

    @GetMapping("/roles")
    @Operation(
            summary = "Retrieve a list of all roles",
            description = "Must have an ADMIN role to have access to this endpoint",
            responses = @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            schemaProperties = @SchemaProperty(
                                    name = "roles",
                                    array = @ArraySchema(
                                            schema = @Schema(
                                                    implementation = RoleResponse.class)
                                    )
                            )
                    )
            )
    )
    public Map<String, List<RoleResponse>> getAllRoles() {
        List<RoleResponse> roles = this.roleService.getAllRoles()
                .stream()
                .map(r -> new RoleResponse(r.getType().name()))
                .toList();
        return Map.of("roles", roles);
    }
}
