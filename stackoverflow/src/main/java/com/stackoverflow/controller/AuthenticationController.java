package com.stackoverflow.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stackoverflow.bo.User;
import com.stackoverflow.dto.auth.LoginUserDto;
import com.stackoverflow.dto.auth.RegisterUserDto;
import com.stackoverflow.dto.responses.LoginResponse;
import com.stackoverflow.service.login.AuthenticationService;
import com.stackoverflow.service.login.JwtService;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.login(loginUserDto);

        List<String> roles = authenticationService.getUserRoles(authenticatedUser);

        String jwtToken = jwtService.generateToken(
                new org.springframework.security.core.userdetails.User(
                        authenticatedUser.getEmail(),
                        authenticatedUser.getPassword(),
                        Collections.emptyList()
                ),
                roles
        );

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());
        loginResponse.setRoles(roles);

        return ResponseEntity.ok(loginResponse);
    }
}
