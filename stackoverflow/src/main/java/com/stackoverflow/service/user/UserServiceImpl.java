package com.stackoverflow.service.user;

import com.stackoverflow.bo.User;
import com.stackoverflow.dto.user.UserRequestUpdate;
import com.stackoverflow.dto.user.UserResponse;
import com.stackoverflow.repository.UserRepository;
import com.stackoverflow.util.UserConvert;
import com.stackoverflow.util.ValidationUtil;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;  

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserConvert userConvert;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<UserResponse> getAllUsers(int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> usersPage = userRepository.findAll(pageable);

        return usersPage.map(this::convertToUserResponse);
    }

    public Optional<UserResponse> getUserById(Long id){
        Optional<User> userFound = userRepository.findById(id);
        return userFound.map(userConvert::UserToUserResponse);
    }

    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }

    public UserResponse updateUser(Long id, UserRequestUpdate userRequestUpdate){
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));

        ValidationUtil.validateNotEmpty(userRequestUpdate.getName(), "Name");
        ValidationUtil.validateNotEmpty(userRequestUpdate.getSurname(), "Surname");
        ValidationUtil.validateNotEmpty(userRequestUpdate.getUsername(), "Username");

        ValidationUtil.validateMaxLength(userRequestUpdate.getName(), 50, "Name");
        ValidationUtil.validateMaxLength(userRequestUpdate.getSurname(), 50, "Surname");
        ValidationUtil.validateMaxLength(userRequestUpdate.getUsername(), 50, "Username");

        ValidationUtil.validateUsername(userRequestUpdate.getUsername());

        if(userRepository.findByUsername(userRequestUpdate.getUsername()).isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The username already exists");
        }

        user.setName(userRequestUpdate.getName());
        user.setSurname(userRequestUpdate.getSurname());
        user.setUsername(userRequestUpdate.getUsername());

        return userConvert.UserToUserResponse(userRepository.save(user));
    }

    private UserResponse convertToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .username(user.getUsername())
                .status(user.getStatus())
                .build();
    }

    @Override
    public void updatePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if(!passwordEncoder.matches(oldPassword, user.getPassword())){
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
