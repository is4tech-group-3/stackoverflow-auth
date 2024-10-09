package com.stackoverflow.controller;

import com.stackoverflow.dto.codeverification.EmailDto;
import com.stackoverflow.dto.codeverification.PasswordResetDto;
import com.stackoverflow.dto.codeverification.PasswordResponse;
import com.stackoverflow.dto.user.PasswordUpdate;
import com.stackoverflow.dto.user.UserRequestUpdate;
import com.stackoverflow.dto.user.UserResponse;
import com.stackoverflow.service.login.AuthenticationService;
import com.stackoverflow.service.recoverypassword.CodeVerificationService;
import com.stackoverflow.service.user.UserService;
import com.stackoverflow.util.AuditAnnotation;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final CodeVerificationService codeVerificationService;

    private final String ENTITY_NAME = "USER";

    @AuditAnnotation(ENTITY_NAME)
    @GetMapping
    public Page<UserResponse> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        return userService.getAllUsers(page, size, sortBy, sortDirection);
    }

    @AuditAnnotation(ENTITY_NAME)
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        Optional<UserResponse> user = userService.getUserById(id);

        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @AuditAnnotation(ENTITY_NAME)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @AuditAnnotation(ENTITY_NAME)
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
            @RequestBody UserRequestUpdate userRequestUpdate) {
        UserResponse updateUser = userService.updateUser(id, userRequestUpdate);
        return ResponseEntity.ok(updateUser);
    }

    @PatchMapping("/passwordChange/{id}")
    public ResponseEntity<Void> changePassword(@PathVariable(name = "id") Long id,
            @RequestBody PasswordUpdate passwordUpdate) {
        userService.updatePassword(id, passwordUpdate.getOldPassword(), passwordUpdate.getNewPassword());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password-recovery")
    public ResponseEntity<PasswordResponse> passwordRecovery(@RequestBody EmailDto emailDto) {
        codeVerificationService.sendCodeVerification(emailDto);
        PasswordResponse response = new PasswordResponse("Recovery code successfully sent");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/password-reset")
    public ResponseEntity<PasswordResponse> resetPassword(@RequestBody PasswordResetDto passwordResetDto) {
        codeVerificationService.verifyCodeAndChangePassword(passwordResetDto);
        PasswordResponse response = new PasswordResponse("Password has been updated correctly");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
