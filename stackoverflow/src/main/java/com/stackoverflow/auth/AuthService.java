package com.lesterlopez.spring_security.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lesterlopez.spring_security.jwt.JwtService;
import com.lesterlopez.spring_security.model.Role;
import com.lesterlopez.spring_security.model.User;
import com.lesterlopez.spring_security.model.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserDetails user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String token =  jwtService.getToken(user);
        return AuthResponse.builder()
            .token(token)
            .build();
    }

    public AuthResponse register(RegisterRequest request) {
        User user = User.builder()
            .name(request.getName())
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.USER)
            .build();
        userRepository.save(user);

        return  AuthResponse.builder()
            .token(jwtService.getToken(user))
            .build();
    }

}
