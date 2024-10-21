package com.stackoverflow.controller;

import com.stackoverflow.bo.User;
import com.stackoverflow.controller.AuthenticationController;
import com.stackoverflow.dto.auth.LoginUserDto;
import com.stackoverflow.dto.auth.RegisterUserDto;
import com.stackoverflow.dto.responses.LoginResponse;
import com.stackoverflow.service.login.AuthenticationService;
import com.stackoverflow.service.login.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;

public class AuthenticationControllerTest {

    private AuthenticationController authenticationController;
    private AuthenticationService authenticationService;
    private JwtService jwtService;

    @BeforeEach
    public void setup() {
        authenticationService = mock(AuthenticationService.class);
        jwtService = mock(JwtService.class);
        authenticationController = new AuthenticationController(jwtService, authenticationService);
    }

    @Test
    public void testRegisterSuccessful() {
        RegisterUserDto registerDto = new RegisterUserDto();
        registerDto.setName("Juan");
        registerDto.setSurname("Ramirez");
        registerDto.setEmail("jramirez@example.com");
        registerDto.setUsername("jramirez7$");
        registerDto.setPassword("Pa$$word7!");

        User registeredUser = new User();
        registeredUser.setId(1L);
        registeredUser.setName(registerDto.getName());
        registeredUser.setEmail(registerDto.getEmail());

        when(authenticationService.signup(registerDto)).thenReturn(registeredUser);

        ResponseEntity<User> response = authenticationController.register(registerDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(registeredUser, response.getBody());
        verify(authenticationService).signup(registerDto);
    }

    @Test
    public void testRegisterFailure() {
        RegisterUserDto registerDto = new RegisterUserDto();
        when(authenticationService.signup(any())).thenThrow(new RuntimeException("Registration failed"));
        assertThrows(RuntimeException.class, () -> authenticationController.register(registerDto));
    }

    @Test
    public void testLoginSuccessful() {
        LoginUserDto loginDto = new LoginUserDto();
        loginDto.setEmail("user@example.com");
        loginDto.setPassword("password123");

        User authenticatedUser = new User(
                1L,
                "Juan",
                "Ramirez",
                loginDto.getEmail(),
                loginDto.getPassword(),
                "ExampleUrlPhotoProfile.png",
                true,
                LocalDate.now(),
                1L,
                null
        );

        String jwtToken = "jwt.token.here";

        when(authenticationService.login(loginDto)).thenReturn(authenticatedUser);
        when(authenticationService.getUserRoles(authenticatedUser)).thenReturn(List.of("ROLE_USER"));
        when(jwtService.generateToken(any(), any(), any(), any())).thenReturn(jwtToken);
        when(jwtService.getExpirationTime()).thenReturn(3600L);

        ResponseEntity<LoginResponse> response = authenticationController.authenticate(loginDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(jwtToken, response.getBody().getToken());
        assertEquals(3600L, response.getBody().getExpiresIn());

        verify(authenticationService).login(loginDto);
        verify(jwtService).generateToken(any(), any(), any(), any());
    }

    @Test
    public void testLoginFailure() {
        LoginUserDto loginDto = new LoginUserDto();
        loginDto.setEmail("juanramirez@example.com");
        loginDto.setPassword("passwordIncorrect");

        when(authenticationService.login(loginDto)).thenThrow(new RuntimeException("Login failed"));

        assertThrows(RuntimeException.class, () -> authenticationController.authenticate(loginDto));
    }
}
