package com.stackoverflow.service.login;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.stackoverflow.bo.User;

import com.stackoverflow.dto.auth.LoginUserDto;
import com.stackoverflow.dto.auth.RegisterUserDto;
import com.stackoverflow.repository.UserRepository;

import com.stackoverflow.util.ValidationUtil;

@Service
public class AuthenticationService {
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

    public User authenticate(LoginUserDto input) {
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

    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }

    public Optional<User> getUserById(Integer id){
        return  userRepository.findById(id);
    }

    public void deleteUser(Integer id){
        userRepository.deleteById(id);
    }

    public User updateUser(Integer id, RegisterUserDto input){
        User user = userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if(input.getName() != null){
            user.setName(input.getName());
        }
        
        user.setSurname(input.getUsername());
        user.setEmail(input.getEmail());
        user.setUsername(input.getUsername());
        if (input.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(input.getPassword()));
        }

        return userRepository.save(user);
    }

}
