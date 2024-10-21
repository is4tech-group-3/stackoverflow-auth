package com.stackoverflow.service.user;

import com.stackoverflow.bo.User;
import com.stackoverflow.dto.user.UserPhotoRequest;
import com.stackoverflow.dto.user.UserRequestUpdate;
import com.stackoverflow.dto.user.UserResponse;
import com.stackoverflow.repository.ProfileRepository;
import com.stackoverflow.repository.UserRepository;
import com.stackoverflow.service.s3.S3Service;
import com.stackoverflow.util.UserConvert;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private UserConvert userConvert;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Validator validator;

    @InjectMocks
    private UserServiceImpl userService;

    @InjectMocks
    S3Service s3Service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllUsers() {
        PageRequest pageRequest = PageRequest.of(0, 8, Sort.by("name").descending());
        Page<User> usersPage = new PageImpl<>(Collections.emptyList());

        when(userRepository.findAll(pageRequest)).thenReturn(usersPage);

        Page<UserResponse> result = userService.getAllUsers(0, 8, "name", "desc");

        assertEquals(0, result.getTotalElements());
        verify(userRepository, times(1)).findAll(pageRequest);
    }

    @Test
    public void testGetUserByIdUserExists() {
        User user = new User();
        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .name("John")
                .surname("Doe")
                .email("jdoe@example.com")
                .username("johndoe")
                .idProfile(1L)
                .status(true)
                .image(null)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userConvert.userToUserResponse(user)).thenReturn(userResponse);  // Cambiado aquí

        Optional<UserResponse> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(userResponse, result.get());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetUserByIdUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<UserResponse> result = userService.getUserById(1L);

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testDeleteUser() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testUpdateUserUserExists() {
        User user = new User();
        user.setId(1L);
        user.setName("John");
        user.setSurname("Doe");
        user.setEmail("jdoe@example.com");
        user.setUsername("johndoe");
        user.setProfileId(1L);
        user.setProfilePhoto(null);

        UserResponse expectedResponse = UserResponse.builder()
                .id(1L)
                .name("John")
                .surname("Doe")
                .email("jdoe@example.com")
                .username("johndoe")
                .idProfile(1L)
                .image(null)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userConvert.userToUserResponse(user)).thenReturn(expectedResponse);  // Cambiado aquí

        UserRequestUpdate userRequestUpdate = new UserRequestUpdate();
        userRequestUpdate.setName("John");
        userRequestUpdate.setSurname("Doe");

        UserResponse result = userService.updateUser(1L, userRequestUpdate);

        assertNotNull(result, "The result must not be null");
        assertEquals(expectedResponse, result, "The UserResponse object must match the expected one.");

        verify(userRepository, times(1)).save(user);
        verify(userConvert, times(1)).userToUserResponse(user);  // Cambiado aquí
    }

    @Test
    public void testUpdateUserUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.updateUser(1L, new UserRequestUpdate());
        });

        assertEquals("User not found with ID: 1", exception.getMessage());
    }

    @Test
    public void testUpdatePassword() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        User userPrincipal = new User();
        userPrincipal.setId(1L);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);

        User user = new User();
        user.setPassword(passwordEncoder.encode("StrongP@ss1"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("StrongNewP@ss1")).thenReturn("newEncodedPassword");

        userService.updatePassword("oldPassword", "StrongNewP@ss1");

        verify(userRepository, times(1)).save(user);
        assertEquals("newEncodedPassword", user.getPassword());
    }

    @Test
    public void testUpdatePasswordIncorrectOldPassword() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        User userPrincipal = new User();
        userPrincipal.setId(1L);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);

        User user = new User();
        user.setId(1L);
        user.setPassword("encodedPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", user.getPassword())).thenReturn(false);

        assertThrows(ConstraintViolationException.class, () -> {
            userService.updatePassword("oldPassword", "newPassword");
        });

        verify(userRepository, never()).save(user);
    }



    @Test
    public void testGetUserByUsernameUserExists() {
        User user = new User();
        user.setId(1L);
        user.setUsername("johndoe");

        UserResponse expectedResponse = UserResponse.builder()
                .id(1L)
                .username("johndoe")
                .build();

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(user));
        when(userConvert.userToUserResponse(user)).thenReturn(expectedResponse);

        Optional<UserResponse> result = userService.getUserByUsername("johndoe");

        assertTrue(result.isPresent());
        assertEquals(expectedResponse, result.get());
        verify(userRepository, times(1)).findByUsername("johndoe");
    }

    @Test
    public void testGetUserByUsernameUserDoesNotExist() {
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.empty());

        Optional<UserResponse> result = userService.getUserByUsername("johndoe");

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByUsername("johndoe");
    }

    @Test
    public void testGetUserByEmailUserExists() {
        User user = new User();
        user.setId(1L);
        user.setEmail("jdoe@example.com");

        UserResponse expectedResponse = UserResponse.builder()
                .id(1L)
                .email("jdoe@example.com")
                .build();

        when(userRepository.findByEmail("jdoe@example.com")).thenReturn(Optional.of(user));
        when(userConvert.userToUserResponse(user)).thenReturn(expectedResponse);

        Optional<UserResponse> result = userService.getUserByEmail("jdoe@example.com");

        assertTrue(result.isPresent());
        assertEquals(expectedResponse, result.get());
        verify(userRepository, times(1)).findByEmail("jdoe@example.com");
    }

    @Test
    public void testGetUserByEmailUserDoesNotExist() {
        when(userRepository.findByEmail("jdoe@example.com")).thenReturn(Optional.empty());

        Optional<UserResponse> result = userService.getUserByEmail("jdoe@example.com");

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByEmail("jdoe@example.com");
    }

    @Test
    public void testUpdateProfileUserUserDoesNotExist() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.updateProfileUser(1L, 1L);
        });

        assertEquals("User not found with ID: 1", exception.getMessage());
    }

    @Test
    public void testUpdateProfileUserProfileDoesNotExist() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(profileRepository.existsById(1L)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.updateProfileUser(1L, 1L);
        });

        assertEquals("Profile not found with ID: 1", exception.getMessage());
    }

    @Test
    public void testChangeStatusUser() {
        User user = new User();
        user.setId(1L);
        user.setStatus(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(userRepository.save(user)).thenReturn(user);
        when(userConvert.userToUserResponse(user)).thenReturn(UserResponse.builder()
                .id(1L)
                .status(false)
                .build());

        UserResponse result = userService.changeStatusUser(1L);

        assertNotNull(result, "The result must not be null");
        assertFalse(result.getStatus(), "The user's status must be false after the change");
        verify(userRepository, times(1)).save(user);
    }


    @Test
    public void testChangeStatusUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.changeStatusUser(1L);
        });

        assertEquals("User not found with ID: 1", exception.getMessage());
    }

    @Test
    public void testChangeStatusUserAlreadyDisabled() {
        User user = new User();
        user.setId(1L);
        user.setStatus(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(userRepository.save(user)).thenReturn(user);
        when(userConvert.userToUserResponse(user)).thenReturn(UserResponse.builder()
                .id(1L)
                .status(true)
                .build());

        UserResponse result = userService.changeStatusUser(1L);

        assertNotNull(result, "The result must not be null");
        assertTrue(result.getStatus(), "The user status must be true after the change");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testUpdateProfileUserProfileInvalid() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(profileRepository.existsById(999L)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.updateProfileUser(1L, 999L);
        });

        assertEquals("Profile not found with ID: 999", exception.getMessage());
    }

    @Test
    public void testGetAllUsersWithUsers() {
        Page<User> usersPage = new PageImpl<>(List.of(new User(), new User()));

        when(userRepository.findAll(any(PageRequest.class))).thenReturn(usersPage);

        Page<UserResponse> result = userService.getAllUsers(0, 8, "name", "desc");

        assertEquals(2, result.getTotalElements());
        verify(userRepository, times(1)).findAll(any(PageRequest.class));
    }


    @Test
    public void testChangePhotoProfileUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserPhotoRequest userPhotoRequest = new UserPhotoRequest();
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.changePhotoProfile(1L, userPhotoRequest);
        });

        assertEquals("User not found with ID: 1", exception.getMessage());
    }

    @Test
    public void testUpdatePasswordInvalidNewPassword() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        User userPrincipal = new User();
        userPrincipal.setId(1L);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);

        User user = new User();
        user.setPassword(passwordEncoder.encode("StrongP@ss1"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", user.getPassword())).thenReturn(true);

        String invalidNewPassword = "weakpassword";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updatePassword("oldPassword", invalidNewPassword);
        });

        assertEquals("New password does not meet security requirements", exception.getMessage());
        verify(userRepository, never()).save(user);
    }

    @Test
    public void testGetUserByUsernameDoesNotExist() {
        when(userRepository.findByUsername("unknownuser")).thenReturn(Optional.empty());

        Optional<UserResponse> result = userService.getUserByUsername("unknownuser");

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByUsername("unknownuser");
    }

    @Test
    public void testGetUserByIdUnexpectedException() {
        when(userRepository.findById(1L)).thenThrow(new RuntimeException("Unexpected Error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserById(1L);
        });

        assertEquals("Unexpected Error", exception.getMessage());
    }

    @Test
    public void testUpdateProfileUserDatabaseError() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(profileRepository.existsById(1L)).thenThrow(new RuntimeException("Database Error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateProfileUser(1L, 1L);
        });

        assertEquals("Database Error", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testChangeStatusUserAccessDenied() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("unauthorizedUser");
        SecurityContextHolder.setContext(securityContext);

        doThrow(new AccessDeniedException("Access Denied")).when(userRepository).findById(anyLong());

        assertThrows(AccessDeniedException.class, () -> {
            userService.changeStatusUser(1L);
        });
    }

    @Test
    void testUpdateUser_WithTooLongFields_ShouldThrowException() {
        Long userId = 1L;
        String longName = "a".repeat(100);
        String longSurname = "b".repeat(100);

        UserRequestUpdate requestUpdate = new UserRequestUpdate();
        requestUpdate.setName(longName);
        requestUpdate.setSurname(longSurname);

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doThrow(new ConstraintViolationException(Set.of())).when(validator).validate(user);

        assertThrows(ConstraintViolationException.class, () -> userService.updateUser(userId, requestUpdate));
    }

    @Test
    void testUpdateUser_WithNullFields_ShouldThrowException() {
        Long userId = 1L;
        UserRequestUpdate requestUpdate = new UserRequestUpdate();
        requestUpdate.setName(null);
        requestUpdate.setSurname(null);

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doThrow(new ConstraintViolationException(Set.of())).when(validator).validate(user);

        assertThrows(ConstraintViolationException.class, () -> userService.updateUser(userId, requestUpdate));
    }


    @Test
    void testUpdateUser_WithEmptyFields_ShouldThrowException() {
        Long userId = 1L;
        UserRequestUpdate requestUpdate = new UserRequestUpdate();
        requestUpdate.setName("");
        requestUpdate.setSurname("");

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doThrow(new ConstraintViolationException(Set.of())).when(validator).validate(user);

        assertThrows(ConstraintViolationException.class, () -> userService.updateUser(userId, requestUpdate));
    }


    @Test
    void testGetUserById_UserNotFound() {
        Long nonExistentUserId = 999L;
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        Optional<UserResponse> userResponse = userService.getUserById(nonExistentUserId);

        assertTrue(userResponse.isEmpty(), "User should not be found");
    }

    @Test
    void testGetUserByUsername_UserNotFound() {
        String nonExistentUsername = "nonexistent";
        when(userRepository.findByUsername(nonExistentUsername)).thenReturn(Optional.empty());

        Optional<UserResponse> userResponse = userService.getUserByUsername(nonExistentUsername);

        assertTrue(userResponse.isEmpty(), "User should not be found");
    }

    @Test
    void testGetUserByEmail_UserNotFound() {
        String nonExistentEmail = "nonexistent@example.com";
        when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        Optional<UserResponse> userResponse = userService.getUserByEmail(nonExistentEmail);

        assertTrue(userResponse.isEmpty(), "User should not be found");
    }
}
