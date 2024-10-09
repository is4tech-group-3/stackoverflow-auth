package com.stackoverflow.service.user;

import com.stackoverflow.dto.user.UserRequestUpdate;
import com.stackoverflow.dto.user.UserResponse;

import java.util.Optional;

import org.springframework.data.domain.Page;

public interface UserService {
    Page<UserResponse> getAllUsers(int page, int size, String sortBy, String sortDirection);
    Optional<UserResponse> getUserById(Long id);
    void deleteUser(Long id);
    UserResponse updateUser(Long id, UserRequestUpdate userRequestUpdate);
    void updatePassword(Long id, String oldPassword, String newPassword);
}
