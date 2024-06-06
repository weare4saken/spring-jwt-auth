package org.weare4saken.spring_jwt_auth.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Tag(name = "User")
@RestController
@RequestMapping("/api/v1/user")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class UserController {

    @GetMapping(value = "/welcome", produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(
            summary = "Welcome endpoint",
            description = "Returns a welcome message for the authorized user",
            responses = @ApiResponse(
                    responseCode = "200",
                    content = @Content(examples = @ExampleObject("Welcome, User!"))
            )
    )
    public String welcome(Principal principal) {
        return "Welcome, " + principal.getName() + "!";
    }
}
