package com.stackoverflow.service.user;

import com.stackoverflow.dto.user.UserPhotoRequest;
import com.stackoverflow.dto.user.UserRequestUpdate;
import com.stackoverflow.dto.user.UserResponse;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    Page<UserResponse> getAllUsers(int page, int size, String sortBy, String sortDirection);
    Optional<UserResponse> getUserById(Long id);
    void deleteUser(Long id);
    UserResponse updateUser(Long id, UserRequestUpdate userRequestUpdate);
    UserResponse updateProfileUser(Long userId, Long profileId);
    void updatePassword(String oldPassword, String newPassword);
    Optional<UserResponse> getUserByUsername(String username);
    Optional<UserResponse> getUserByEmail(String email);
    UserResponse changeStatusUser(Long idUser);
    UserResponse changePhotoProfile(UserPhotoRequest userPhotoRequest);
}