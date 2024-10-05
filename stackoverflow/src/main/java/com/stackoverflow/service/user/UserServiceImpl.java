package com.stackoverflow.service.user;

import com.stackoverflow.bo.User;
import com.stackoverflow.dto.user.UserRequestUpdate;
import com.stackoverflow.dto.user.UserResponse;
import com.stackoverflow.repository.UserRepository;
import com.stackoverflow.util.UserConvert;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserConvert userConvert;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(userConvert::UserToUserResponse).collect(Collectors.toList());
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

        user.setName(userRequestUpdate.getName());
        user.setSurname(userRequestUpdate.getSurname());
        user.setUsername(userRequestUpdate.getUsername());

        return userConvert.UserToUserResponse(userRepository.save(user));
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
