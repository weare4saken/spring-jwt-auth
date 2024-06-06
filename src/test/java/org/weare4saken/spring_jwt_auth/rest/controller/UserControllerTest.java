package org.weare4saken.spring_jwt_auth.rest.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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

import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AuthController.class, UserController.class})
@Import({SecurityConfiguration.class})
class UserControllerTest {

    static final String USER_API = "/api/v1/user";
    static final String WELCOME_PATH = "/welcome";

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

    @Test
    void givenUserIsNotAuthenticated_whenGetWelcome_thenAccessIsForbidden() throws Exception {
        this.mockMvc.perform(get(UserControllerTest.USER_API + UserControllerTest.WELCOME_PATH))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("{\"detail\": \"Full authentication is required to access this resource\"}")
                );
    }

    @MethodSource("signInRequests")
    @ParameterizedTest
    void givenUserIsAuthenticated_whenGetWelcome_thenReturnsWelcomeMessage(SignInRequest signInRequest, User user) throws Exception {
        when(this.userRepository.findByUsername(signInRequest.getUsername())).thenReturn(Optional.of(user));

        String token = AuthControllerTest.fetchToken(this.mockMvc, signInRequest);

        this.mockMvc.perform(get(UserControllerTest.USER_API + UserControllerTest.WELCOME_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN),
                        content().string("Welcome, " + signInRequest.getUsername() + "!")
                );
    }

    @MethodSource("signInRequests")
    @ParameterizedTest
    void givenUserUsesInvalidToken_whenGetWelcome_thenAccessIsForbidden(SignInRequest signInRequest, User user) throws Exception {
        when(this.userRepository.findByUsername(signInRequest.getUsername())).thenReturn(Optional.of(user));

        String token = AuthControllerTest.fetchToken(this.mockMvc, signInRequest);
        token = token.replaceAll(".$", "");

        this.mockMvc.perform(get(UserControllerTest.USER_API + UserControllerTest.WELCOME_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        content().string(Matchers.containsString("Bad credentials"))
                );
    }

    public static Stream<Arguments> signInRequests() {
        return Stream.of(
                Arguments.of(AuthControllerTest.SIGN_IN_REQUESTS.get("user"), AuthControllerTest.USERS.get("user")),
                Arguments.of(AuthControllerTest.SIGN_IN_REQUESTS.get("admin"), AuthControllerTest.USERS.get("admin"))
        );
    }
}
