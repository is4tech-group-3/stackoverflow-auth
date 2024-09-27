package com.stackoverflow.service.login;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.stackoverflow.bo.User;

import com.stackoverflow.dto.auth.LoginUserDto;
import com.stackoverflow.dto.auth.RegisterUserDto;
import com.stackoverflow.repository.UserRepository;

import com.stackoverflow.util.ValidationUtil;

@Service
@AllArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public User signup(RegisterUserDto input) {
        ValidationUtil.validateNotEmpty(input.getName(), "Name");
        ValidationUtil.validateNotEmpty(input.getSurname(), "Surname");
        ValidationUtil.validateNotEmpty(input.getEmail(), "Email");
        ValidationUtil.validateNotEmpty(input.getUsername(), "Username");
        ValidationUtil.validateNotEmpty(input.getPassword(), "Password");

        ValidationUtil.validationEmailFormat(input.getEmail());

        ValidationUtil.validateUniqueEmail(userRepository, input.getEmail());

        ValidationUtil.validatePassword(input.getPassword());

        User user = new User();
        user.setName(input.getName());
        user.setSurname(input.getSurname());
        user.setEmail(input.getEmail());
        user.setUsername(input.getUsername());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setStatus(true);

        return userRepository.save(user);
    }

    public User login(LoginUserDto input) {
        ValidationUtil.validateNotEmpty(input.getEmail(), "Email");
        ValidationUtil.validateNotEmpty(input.getPassword(), "Password");

        ValidationUtil.validationEmailFormat(input.getEmail());
    
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()));
    
        return userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
