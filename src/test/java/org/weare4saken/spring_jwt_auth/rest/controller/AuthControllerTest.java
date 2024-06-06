package org.weare4saken.spring_jwt_auth.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.weare4saken.spring_jwt_auth.config.SecurityConfiguration;
import org.weare4saken.spring_jwt_auth.entity.Role;
import org.weare4saken.spring_jwt_auth.entity.RoleType;
import org.weare4saken.spring_jwt_auth.entity.User;
import org.weare4saken.spring_jwt_auth.exception.JwtAuthenticationEntryPoint;
import org.weare4saken.spring_jwt_auth.repository.RoleRepository;
import org.weare4saken.spring_jwt_auth.repository.UserRepository;
import org.weare4saken.spring_jwt_auth.rest.model.SignInRequest;
import org.weare4saken.spring_jwt_auth.rest.model.SignUpRequest;
import org.weare4saken.spring_jwt_auth.service.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({SecurityConfiguration.class})
class AuthControllerTest {

    static final String AUTH_API = "/api/v1/auth";
    static final String SIGN_IN = "/sign-in";
    static final String SIGN_UP = "/sign-up";

    static final Map<String, User> USERS = new HashMap<>();
    static final Map<String, SignInRequest> SIGN_IN_REQUESTS = new HashMap<>();

    public static final String USER_KEY = "user";

    public static final String ADMIN_KEY = "admin";

    static {
        SignInRequest signInUserRequest = new SignInRequest("user", "password");
        User dbUserData =
                new org.weare4saken.spring_jwt_auth.entity.User(
                        1L, "user",
                        "user@email.com",
                        "$2y$10$u7XrM8iGgdD3E4VtH.wikOQIhcFEsB0mCQpdfBNzLS./xOmXgdvU6",
                        List.of(new Role(RoleType.ROLE_USER))
                );

        SignInRequest signInAdminRequest = new SignInRequest("admin", "password");
        User dbAdminData =
                new org.weare4saken.spring_jwt_auth.entity.User(
                        2L, "admin",
                        "admin@email.com",
                        "$2y$10$u7XrM8iGgdD3E4VtH.wikOQIhcFEsB0mCQpdfBNzLS./xOmXgdvU6",
                        List.of(new Role(RoleType.ROLE_USER), new Role(RoleType.ROLE_ADMIN))
                );

        AuthControllerTest.USERS.put(AuthControllerTest.USER_KEY, dbUserData);
        AuthControllerTest.USERS.put(AuthControllerTest.ADMIN_KEY, dbAdminData);
        AuthControllerTest.SIGN_IN_REQUESTS.put(AuthControllerTest.USER_KEY, signInUserRequest);
        AuthControllerTest.SIGN_IN_REQUESTS.put(AuthControllerTest.ADMIN_KEY, signInAdminRequest);
    }

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

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void givenUserSignsUp_whenSignedUp_thenTokenIsReturned() throws Exception {
        when(this.userRepository.existsByUsername(anyString())).thenReturn(false);
        when(this.userRepository.existsByEmail(anyString())).thenReturn(false);
        when(this.userRepository.save(any())).then(invocation -> invocation.getArgument(0));
        when(this.roleRepository.findRolesByTypeIn(any())).thenReturn(List.of(new Role(RoleType.ROLE_USER)));
        SignUpRequest signUpRequest = new SignUpRequest("test", "test@test.com", "password");

        String jsonContent = this.objectMapper.writeValueAsString(signUpRequest);
        this.mockMvc
                .perform(post(AuthControllerTest.AUTH_API + AuthControllerTest.SIGN_UP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                )
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.token").exists()
                );
    }

    @CsvSource(value = {"'':test@test.com:password", "test:'':password", "test:test@test.com:''"}, delimiter = ':')
    @ParameterizedTest
    void givenUserEntersInvalidData_whenSignUp_thenErrorIsReturned(String username, String email, String password) throws Exception {
        when(this.userRepository.existsByUsername(anyString())).thenReturn(false);
        when(this.userRepository.existsByEmail(anyString())).thenReturn(false);
        when(this.userRepository.save(any())).then(invocation -> invocation.getArgument(0));
        when(this.roleRepository.findRolesByTypeIn(any())).thenReturn(List.of(new Role(RoleType.ROLE_USER)));
        SignUpRequest signUpRequest = new SignUpRequest(username, email, password);

        String jsonContent = this.objectMapper.writeValueAsString(signUpRequest);
        this.mockMvc
                .perform(post(AuthControllerTest.AUTH_API + AuthControllerTest.SIGN_UP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                )
                .andDo(print())
                .andExpectAll(
                        status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                );
    }

    @Test
    void givenUserSignsIn_whenSignedIn_thenTokenIsReturned() throws Exception {
        SignInRequest signInRequest = AuthControllerTest.SIGN_IN_REQUESTS.get("user");
        User user = AuthControllerTest.USERS.get("user");
        when(this.userRepository.findByUsername(signInRequest.getUsername())).thenReturn(Optional.of(user));

        String jsonContent = this.objectMapper.writeValueAsString(signInRequest);
        this.mockMvc
                .perform(post(AuthControllerTest.AUTH_API + AuthControllerTest.SIGN_IN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                )
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.token").exists()
                );
    }

    @Test
    void givenUserNotExists_whenSignIn_thenErrorIsReturned() throws Exception {
        SignInRequest signInRequest = new SignInRequest("unknown", "password");
        when(this.userRepository.findByUsername(signInRequest.getUsername())).thenReturn(Optional.empty());

        String jsonContent = this.objectMapper.writeValueAsString(signInRequest);
        this.mockMvc
                .perform(post(AuthControllerTest.AUTH_API + AuthControllerTest.SIGN_IN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("{\"detail\": \"Bad credentials\"}")
                );
    }

    @CsvSource(value = {"'':password", "test:''"}, delimiter = ':')
    @ParameterizedTest
    void givenUserEntersInvalidData_whenSignIn_thenErrorIsReturned(String username, String password) throws Exception {
        SignInRequest signInRequest = new SignInRequest(username, password);

        String jsonContent = this.objectMapper.writeValueAsString(signInRequest);
        this.mockMvc
                .perform(post(AuthControllerTest.AUTH_API + AuthControllerTest.SIGN_IN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                )
                .andDo(print())
                .andExpectAll(
                        status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                );
    }

    public static String fetchToken(MockMvc mockMvc, SignInRequest signInRequest) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String jsonContent = mapper.writeValueAsString(signInRequest);
        MvcResult result = mockMvc.perform(post(AuthControllerTest.AUTH_API + AuthControllerTest.SIGN_IN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk()).andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        return mapper.readTree(contentAsString).path("token").asText();
    }
}