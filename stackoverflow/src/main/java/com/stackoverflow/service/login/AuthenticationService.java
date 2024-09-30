package com.stackoverflow.service.login;

import com.stackoverflow.service.MailService;
import com.stackoverflow.util.LoggerUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.stackoverflow.bo.User;

import com.stackoverflow.dto.auth.LoginUserDto;
import com.stackoverflow.dto.auth.RegisterUserDto;
import com.stackoverflow.repository.UserRepository;

import com.stackoverflow.util.ValidationUtil;

import java.security.SecureRandom;

@Service
public class AuthenticationService {

    @Autowired
    private MailService mailService;

    @Autowired
    private HttpServletRequest request;

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

    public String generatePassword(){
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
        ValidationUtil.validateNotEmpty(input.getName(), "Name");
        ValidationUtil.validateNotEmpty(input.getSurname(), "Surname");
        ValidationUtil.validateNotEmpty(input.getEmail(), "Email");
        ValidationUtil.validateNotEmpty(input.getUsername(), "Username");

        String password = generatePassword();
        ValidationUtil.validationEmailFormat(input.getEmail());
        ValidationUtil.validateUniqueEmail(userRepository, input.getEmail());
        ValidationUtil.validatePassword(password);

        User user = new User();
        user.setName(input.getName());
        user.setSurname(input.getSurname());
        user.setEmail(input.getEmail());
        user.setUsername(input.getUsername());
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus(true);

        String to = input.getEmail();
        String nameUser = input.getName();

        mailService.sendEmailWelcome(to, password, nameUser);

        LoggerUtil.loggerInfo(request, HttpStatus.CREATED, "Usuario registrado");

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
