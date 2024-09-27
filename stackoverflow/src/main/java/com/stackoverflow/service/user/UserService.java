package com.stackoverflow.service.user;

import com.stackoverflow.bo.User;
import com.stackoverflow.dto.user.UserRequestUpdate;
import com.stackoverflow.dto.user.UserResponse;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserResponse> getAllUsers();
    Optional<UserResponse> getUserById(Long id);
    void deleteUser(Long id);
    UserResponse updateUser(Long id, UserRequestUpdate userRequestUpdate);
}
