package org.weare4saken.spring_jwt_auth.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.weare4saken.spring_jwt_auth.rest.model.AccessTokenResponse;
import org.weare4saken.spring_jwt_auth.rest.model.SignInRequest;
import org.weare4saken.spring_jwt_auth.rest.model.SignUpRequest;
import org.weare4saken.spring_jwt_auth.service.AuthenticationService;

import javax.naming.AuthenticationException;

@Tag(name = "Authentication")
@ApiResponse(
        responseCode = "200",
        content = @Content(
                schema = @Schema(
                        implementation = AccessTokenResponse.class
                )
        )
)
@RestController
@RequestMapping(path = "/api/v1/auth",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Register a new user")
    @PostMapping("/sign-up")
    public AccessTokenResponse signUp(@RequestBody @Valid SignUpRequest request) {
        return this.authenticationService.signUp(request);
    }

    @Operation(summary = "Authenticate a user")
    @PostMapping("/sign-in")
    public AccessTokenResponse signIn(@RequestBody @Valid SignInRequest request) throws AuthenticationException {
        return this.authenticationService.signIn(request);
    }
}
