package com.stackoverflow.service.login;

import com.stackoverflow.bo.Profile;
import com.stackoverflow.bo.User;
import com.stackoverflow.dto.auth.LoginUserDto;
import com.stackoverflow.dto.auth.RegisterUserDto;
import com.stackoverflow.repository.ProfileRepository;
import com.stackoverflow.repository.UserRepository;
import com.stackoverflow.service.MailService;
import com.stackoverflow.service.s3.S3Service;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.persistence.EntityNotFoundException;

import java.security.SecureRandom;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private S3Service s3Service;

    @Mock
    private HttpServletRequest request;

    @Mock
    private MailService mailService;

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
    }

    @Test
    public void signupWhenUserIsValid() {
        RegisterUserDto registerUserDto = new RegisterUserDto();
        registerUserDto.setUsername("newUser");
        registerUserDto.setEmail("newuser@example.com");
        registerUserDto.setName("New");
        registerUserDto.setSurname("User");

        Profile profile = new Profile();
        profile.setIdProfile(1L);
        profile.setName("USER");

        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
        when(profileRepository.findByName("USER")).thenReturn(Optional.of(profile));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = authenticationService.signup(registerUserDto);

        assertNotNull(savedUser);
        assertEquals("newUser", savedUser.getUsername());
        verify(userRepository).save(any(User.class));

        System.out.println("Username saved: " + savedUser.getUsername());
    }

    @Test
    public void signupWhenUsernameExists() {
        RegisterUserDto registerUserDto = new RegisterUserDto();
        registerUserDto.setUsername("existingUser");
        registerUserDto.setEmail("newuser@example.com");

        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(new User()));

        assertThrows(DataIntegrityViolationException.class, () -> authenticationService.signup(registerUserDto));
    }

    @Test
    public void loginWhenCredentialsAreValid() {
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setEmail("user@example.com");
        loginUserDto.setPassword("password");

        User mockUser = new User();
        mockUser.setEmail("user@example.com");

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser));

        User loggedInUser = authenticationService.login(loginUserDto);

        assertNotNull(loggedInUser);
        assertEquals("user@example.com", loggedInUser.getEmail());
    }

    @Test
    public void loginWhenUserDoesNotExist() {
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setEmail("invalid@example.com");
        loginUserDto.setPassword("password");

        when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException());
        when(userRepository.findByEmail("invalid@example.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> authenticationService.login(loginUserDto));
    }
}
