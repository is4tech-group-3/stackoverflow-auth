package com.stackoverflow.controller;

import com.stackoverflow.dto.user.*;
import com.stackoverflow.service.user.UserService;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllUsers() {
        Page<UserResponse> usersPage = new PageImpl<>(Collections.emptyList());
        when(userService.getAllUsers(0, 8, "name", "desc")).thenReturn(usersPage);

        Page<UserResponse> result = userController.getAllUsers(0, 8, "name", "desc");

        assertEquals(0, result.getTotalElements());
        verify(userService, times(1)).getAllUsers(0, 8, "name", "desc");
    }

    @Test
    public void testGetUserByIdUserExists() {
        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .name("Nombre")
                .surname("Apellido")
                .email("correo@example.com")
                .username("usuario")
                .idProfile(123L)
                .status(true)
                .image("ruta/a/imagen.jpg")
                .build();

        when(userService.getUserById(1L)).thenReturn(Optional.of(userResponse));

        ResponseEntity<UserResponse> result = userController.getUserById(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(userResponse, result.getBody());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    public void testGetUserByIdUserDoesNotExist() {
        when(userService.getUserById(1L)).thenReturn(Optional.empty());

        ResponseEntity<UserResponse> result = userController.getUserById(1L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    public void testDeleteUser() {
        doNothing().when(userService).deleteUser(1L);

        ResponseEntity<Void> result = userController.deleteUser(1L);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    public void testUpdateUser() {
        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .name("Nombre")
                .surname("Apellido")
                .email("correo@example.com")
                .username("usuario")
                .idProfile(123L)
                .status(true)
                .image("ruta/a/imagen.jpg")
                .build();

        UserRequestUpdate userRequestUpdate = new UserRequestUpdate();
        userRequestUpdate.setName("Pedro");
        userRequestUpdate.setSurname("Medina");

        when(userService.updateUser(1L, userRequestUpdate)).thenReturn(userResponse);

        ResponseEntity<UserResponse> result = userController.updateUser(1L, userRequestUpdate);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(userResponse, result.getBody());
        verify(userService, times(1)).updateUser(1L, userRequestUpdate);
    }

    @Test
    public void testUpdateUserNotFound() {
        UserRequestUpdate userRequestUpdate = new UserRequestUpdate();
        when(userService.updateUser(anyLong(), any(UserRequestUpdate.class)))
                .thenThrow(new EntityNotFoundException("User not found"));

        assertThrows(EntityNotFoundException.class, () -> {
            userController.updateUser(1L, userRequestUpdate);
        });

        verify(userService, times(1)).updateUser(anyLong(), any(UserRequestUpdate.class));
    }

    @Test
    public void testChangeStatusUser() {
        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .name("Nombre")
                .surname("Apellido")
                .email("correo@example.com")
                .username("usuario")
                .idProfile(123L)
                .status(true)
                .image("ruta/a/imagen.jpg")
                .build();

        when(userService.changeStatusUser(1L)).thenReturn(userResponse);

        ResponseEntity<UserResponse> result = userController.changeStatusUser(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(userResponse, result.getBody());
        verify(userService, times(1)).changeStatusUser(1L);
    }

    @Test
    public void testGetUserByUsernameUserExists() {
        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .name("Nombre")
                .surname("Apellido")
                .email("correo@example.com")
                .username("usuario")
                .idProfile(123L)
                .status(true)
                .image("ruta/a/imagen.jpg")
                .build();

        when(userService.getUserByUsername("usuario")).thenReturn(Optional.of(userResponse));

        ResponseEntity<UserResponse> result = userController.getUserByUsername("usuario");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(userResponse, result.getBody());
        verify(userService, times(1)).getUserByUsername("usuario");
    }

    @Test
    public void testGetUserByUsernameUserDoesNotExist() {
        when(userService.getUserByUsername("usuario")).thenReturn(Optional.empty());

        ResponseEntity<UserResponse> result = userController.getUserByUsername("usuario");

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        verify(userService, times(1)).getUserByUsername("usuario");
    }

    @Test
    public void testGetUserByEmailUserExists() {
        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .name("Nombre")
                .surname("Apellido")
                .email("correo@example.com")
                .username("usuario")
                .idProfile(123L)
                .status(true)
                .image("ruta/a/imagen.jpg")
                .build();

        when(userService.getUserByUsername("correo@example.com")).thenReturn(Optional.of(userResponse));

        ResponseEntity<UserResponse> result = userController.getUserByEmail("correo@example.com");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(userResponse, result.getBody());
        verify(userService, times(1)).getUserByUsername("correo@example.com");
    }

    @Test
    public void testGetUserByEmailUserDoesNotExist() {
        when(userService.getUserByUsername("correo@example.com")).thenReturn(Optional.empty());

        ResponseEntity<UserResponse> result = userController.getUserByEmail("correo@example.com");

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        verify(userService, times(1)).getUserByUsername("correo@example.com");
    }

    @Test
    public void testChangePassword() {
        PasswordUpdate passwordUpdate = new PasswordUpdate();
        passwordUpdate.setOldPassword("oldPassword");
        passwordUpdate.setNewPassword("newPassword");

        ResponseEntity<Void> result = userController.changePassword(passwordUpdate);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(userService, times(1)).updatePassword("oldPassword", "newPassword");
    }

    @Test
    public void testUpdateUserProfile() {
        UserProfileUpdateRequest userProfileUpdateRequest = new UserProfileUpdateRequest();
        userProfileUpdateRequest.setProfileId(456L);

        UserResponse updatedUserResponse = UserResponse.builder()
                .id(1L)
                .name("Nombre")
                .surname("Apellido")
                .email("correo@example.com")
                .username("usuario")
                .idProfile(456L)
                .status(true)
                .image("ruta/a/imagen.jpg")
                .build();

        when(userService.updateProfileUser(1L, 456L)).thenReturn(updatedUserResponse);

        ResponseEntity<UserResponse> result = userController.updateUserProfile(1L, userProfileUpdateRequest);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(updatedUserResponse, result.getBody());
        verify(userService, times(1)).updateProfileUser(1L, 456L);
    }

    @Test
    public void testChangePhotoProfile() {
        MultipartFile mockFile = mock(MultipartFile.class);

        UserPhotoRequest userPhotoRequest = new UserPhotoRequest();
        userPhotoRequest.setImage(mockFile);

        UserResponse updatedUserResponse = UserResponse.builder()
                .id(1L)
                .name("Nombre")
                .surname("Apellido")
                .email("correo@example.com")
                .username("usuario")
                .idProfile(123L)
                .status(true)
                .image("ruta/nueva/imagen.jpg")
                .build();

        when(userService.changePhotoProfile(1L, userPhotoRequest)).thenReturn(updatedUserResponse);

        ResponseEntity<UserResponse> result = userController.changePhotoProfile(1L, userPhotoRequest);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(updatedUserResponse, result.getBody());
        verify(userService, times(1)).changePhotoProfile(1L, userPhotoRequest);
    }


}
