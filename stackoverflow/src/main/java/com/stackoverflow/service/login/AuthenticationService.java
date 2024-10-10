package com.stackoverflow.service.login;

import com.stackoverflow.bo.Profile;
import com.stackoverflow.service.MailService;
import com.stackoverflow.util.LoggerUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.stackoverflow.bo.Role;
import com.stackoverflow.bo.User;

import com.stackoverflow.dto.auth.LoginUserDto;
import com.stackoverflow.dto.auth.RegisterUserDto;
import com.stackoverflow.repository.ProfileRepository;
import com.stackoverflow.repository.UserRepository;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {

    @Autowired
    private MailService mailService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ProfileRepository profileRepository;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String generatePassword() {
        final String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        final String digits = "0123456789";
        final String symbols = "!@#$%^&*()-_=+[]{}<>?/";

        SecureRandom random = new SecureRandom();
        StringBuilder pass = new StringBuilder();

        pass.append(upperCase.charAt(random.nextInt(upperCase.length())));
        pass.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        pass.append(digits.charAt(random.nextInt(digits.length())));
        pass.append(symbols.charAt(random.nextInt(symbols.length())));

        String allChars = upperCase + lowerCase + digits + symbols;
        for (int i = 0; i < 8; i++) {
            pass.append(allChars.charAt(random.nextInt(allChars.length())));
        }
        return pass.toString();
    }

    public User signup(RegisterUserDto input) {
        if (userRepository.findByUsername(input.getUsername()).isPresent())
            throw new DataIntegrityViolationException("Username already exists");
        if (userRepository.findByEmail(input.getEmail()).isPresent())
            throw new DataIntegrityViolationException("Email already exists");
        String password = generatePassword();
        User user = new User();
        user.setName(input.getName());
        user.setSurname(input.getSurname());
        user.setEmail(input.getEmail());
        user.setUsername(input.getUsername());
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus(true);

        Profile defaultProfile = profileRepository.findByName("USER")
                .orElseThrow(() -> new EntityNotFoundException("Default profile 'USER' not found."));

        user.setProfileId(defaultProfile.getIdProfile());

        String to = input.getEmail();
        String nameUser = input.getName();

        mailService.sendEmailWelcome(to, password, nameUser);
        LoggerUtil.loggerInfo(request, HttpStatus.CREATED, "Registered user");

        return userRepository.save(user);
    }

    public User login(LoginUserDto input) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()));

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public List<String> getUserRoles(User user) {
        return profileRepository.findById(user.getProfileId())
                .map(profile -> profile.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

}
