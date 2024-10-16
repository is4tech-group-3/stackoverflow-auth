package com.stackoverflow.service.user;

import com.stackoverflow.bo.Profile;
import com.stackoverflow.bo.User;
import com.stackoverflow.dto.user.UserPhotoRequest;
import com.stackoverflow.dto.user.UserRequestUpdate;
import com.stackoverflow.dto.user.UserResponse;
import com.stackoverflow.repository.ProfileRepository;
import com.stackoverflow.repository.UserRepository;
import com.stackoverflow.service.s3.S3Service;
import com.stackoverflow.util.UserConvert;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserConvert userConvert;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;
    private final ProfileRepository profileRepository;
    private final S3Service s3Service;

    @Override
    public Page<UserResponse> getAllUsers(int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> usersPage = userRepository.findAll(pageable);

        return usersPage.map(this::convertToUserResponse);
    }

    public Optional<UserResponse> getUserById(Long id) {
        Optional<User> userFound = userRepository.findById(id);
        return userFound.map(userConvert::UserToUserResponse);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public UserResponse updateUser(Long id, UserRequestUpdate userRequestUpdate) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));

        user.setName(userRequestUpdate.getName());
        user.setSurname(userRequestUpdate.getSurname());
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty())
            throw new ConstraintViolationException(violations);

        return userConvert.UserToUserResponse(userRepository.save(user));
    }

    private UserResponse convertToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .username(user.getUsername())
                .idProfile(user.getProfileId())
                .status(user.getStatus())
                .image(user.getProfilePhoto())
                .build();
    }

    @Override
    public void updatePassword(String oldPassword, String newPassword) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((User) userDetails).getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty())
            throw new ConstraintViolationException(violations);

        if (!passwordEncoder.matches(oldPassword, user.getPassword()))
            throw new RuntimeException("Old password is incorrect");

        String regex = "^(?=.*\\d)(?=.*[\\u0021-\\u002b\\u003c-\\u0040])(?=.*[A-Z])(?=.*[a-z])\\S{8,16}$";
        if (!newPassword.matches(regex)) {
            throw new IllegalArgumentException("New password does not meet security requirements");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public Optional<UserResponse> getUserByUsername(String username) {
        Optional<User> userFound = userRepository.findByUsername(username);
        return userFound.map(userConvert::UserToUserResponse);
    }

    @Override
    public Optional<UserResponse> getUserByEmail(String email) {
        Optional<User> userFound = userRepository.findByEmail(email);
        return userFound.map(userConvert::UserToUserResponse);
    }

    @Override
    public UserResponse updateProfileUser(Long userId, Long profileId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found with ID: " + profileId));

        user.setProfileId(profileId);

        return userConvert.UserToUserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse changeStatusUser(Long idUser) {
        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + idUser));

        user.setStatus(!user.getStatus());

        User updatedUser = userRepository.save(user);
        return userConvert.UserToUserResponse(updatedUser);
    }

    @Override
    public UserResponse changePhotoProfile(UserPhotoRequest userPhotoRequest) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((User) userDetails).getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (user.getProfilePhoto() != null && !user.getProfilePhoto().isEmpty()) {
            String oldKey = user.getProfilePhoto().substring(user.getProfilePhoto().lastIndexOf("/") + 1);
            try {
                s3Service.deleteObject(oldKey);
            } catch (Exception e) {
                throw new RuntimeException("Error deleting the old image: " + e.getMessage(), e);
            }
        }

        String imageUrl = null;
        if (userPhotoRequest.getImage() != null || !userPhotoRequest.getImage().isEmpty()) {
            String key = s3Service.putObject(userPhotoRequest.getImage());
            imageUrl = s3Service.getObjectUrl(key);
        }

        user.setProfilePhoto(imageUrl);
        User updateUser = userRepository.save(user);

        return userConvert.UserToUserResponse(updateUser);
    }

}
