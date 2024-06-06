package org.weare4saken.spring_jwt_auth.rest.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.weare4saken.spring_jwt_auth.config.SecurityConfiguration;
import org.weare4saken.spring_jwt_auth.entity.User;
import org.weare4saken.spring_jwt_auth.exception.JwtAuthenticationEntryPoint;
import org.weare4saken.spring_jwt_auth.repository.RoleRepository;
import org.weare4saken.spring_jwt_auth.repository.UserRepository;
import org.weare4saken.spring_jwt_auth.rest.model.SignInRequest;
import org.weare4saken.spring_jwt_auth.service.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {AuthController.class, AdminController.class})
@Import({SecurityConfiguration.class})
class AdminControllerTest {

    static final String ADMIN_API = "/api/v1/admin";
    static final String USERS_PATH = "/users";
    static final String ROLES_PATH = "/roles";

    @MockBean
    RoleRepository roleRepository;

    @MockBean
    UserRepository userRepository;

    @SpyBean
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @SpyBean
    JwtService jwtService;

    @SpyBean
    RoleService roleService;

    @SpyBean
    UserService userService;

    @SpyBean
    CustomUserDetailsService userDetailsService;

    @SpyBean
    AuthenticationService authenticationService;

    @Autowired
    MockMvc mockMvc;

    @ValueSource(strings = {AdminControllerTest.USERS_PATH, AdminControllerTest.ROLES_PATH})
    @ParameterizedTest
    void givenUserIsNotAuthenticated_whenGetPaths_thenAccessIsForbidden(String path) throws Exception {
        this.mockMvc.perform(get(AdminControllerTest.ADMIN_API + path))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("{\"detail\": \"Full authentication is required to access this resource\"}")
                );
    }

    @ValueSource(strings = {AdminControllerTest.USERS_PATH, AdminControllerTest.ROLES_PATH})
    @ParameterizedTest
    void givenUserIsAuthenticatedAsUser_whenGetPaths_thenAccessIsForbidden(String path) throws Exception {
        SignInRequest signInRequest = AuthControllerTest.SIGN_IN_REQUESTS.get("user");
        User user = AuthControllerTest.USERS.get("user");

        when(this.userRepository.findByUsername(signInRequest.getUsername())).thenReturn(Optional.of(user));

        String token = AuthControllerTest.fetchToken(this.mockMvc, signInRequest);
        this.mockMvc.perform(get(AdminControllerTest.ADMIN_API + path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("{\"detail\": \"Access Denied\"}")
                );
    }

    @Test
    void givenUserIsAuthenticatedAsAdmin_whenGetAllUsers_thenReturnsAllUsers() throws Exception {
        SignInRequest signInRequest = AuthControllerTest.SIGN_IN_REQUESTS.get("admin");
        User user = AuthControllerTest.USERS.get("admin");

        when(this.userRepository.findByUsername(signInRequest.getUsername())).thenReturn(Optional.of(user));
        when(this.userRepository.findAll()).thenReturn(List.of(user));

        String token = AuthControllerTest.fetchToken(this.mockMvc, signInRequest);
        this.mockMvc.perform(get(AdminControllerTest.ADMIN_API + AdminControllerTest.USERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.users").isArray(),
                        jsonPath("$.users").value(hasItem(
                                Map.of("username", user.getUsername(),
                                        "email", user.getEmail(),
                                        "roles", user.getRoles().stream()
                                                .map(role -> role.getType().toString())
                                                .toList()))
                        )
                );
    }

    @Test
    void givenUserIsAuthenticatedAsAdmin_whenGetAllRoles_thenReturnsAllRoles() throws Exception {
        SignInRequest signInRequest = AuthControllerTest.SIGN_IN_REQUESTS.get("admin");
        User user = AuthControllerTest.USERS.get("admin");

        when(this.userRepository.findByUsername(signInRequest.getUsername())).thenReturn(Optional.of(user));
        when(this.roleRepository.findAll()).thenReturn(user.getRoles());

        String token = AuthControllerTest.fetchToken(this.mockMvc, signInRequest);
        this.mockMvc.perform(get(AdminControllerTest.ADMIN_API + AdminControllerTest.ROLES_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.roles").isArray(),
                        jsonPath("$.roles").value(containsInAnyOrder(
                                        Map.of("role", user.getRoles().getFirst().getType().toString()),
                                        Map.of("role", user.getRoles().get(1).getType().toString())
                                )
                        ));
    }
}
