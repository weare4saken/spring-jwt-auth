package org.weare4saken.spring_jwt_auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.weare4saken.spring_jwt_auth.entity.Role;
import org.weare4saken.spring_jwt_auth.entity.RoleType;
import org.weare4saken.spring_jwt_auth.entity.User;
import org.weare4saken.spring_jwt_auth.rest.model.AccessTokenResponse;
import org.weare4saken.spring_jwt_auth.rest.model.SignInRequest;
import org.weare4saken.spring_jwt_auth.rest.model.SignUpRequest;

import javax.naming.AuthenticationException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AccessTokenResponse signUp(SignUpRequest request) {
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(this.passwordEncoder.encode(request.getPassword()));
        newUser.addRole(new Role(RoleType.ROLE_USER));

        this.userService.createUser(newUser);
        String jwt = this.jwtService.generateToken(newUser);

        return new AccessTokenResponse(jwt);
    }

    public AccessTokenResponse signIn(SignInRequest request) throws AuthenticationException {
        Authentication authenticate = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        User user = this.userService.getByUsername(authenticate.getName());
        String jwt = this.jwtService.generateToken(user);

        return new AccessTokenResponse(jwt);
    }
}
