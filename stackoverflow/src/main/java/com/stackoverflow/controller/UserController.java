package com.stackoverflow.controller;

import com.stackoverflow.dto.ChangePasswordCodeDto;
import com.stackoverflow.dto.CodeVerificationDto;
import com.stackoverflow.dto.EmailDto;
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

import java.util.List;
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
    public Page<UserResponse> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "8") int size) {
        return userService.getAllUsers(page, size);
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
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @AuditAnnotation(ENTITY_NAME)
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserRequestUpdate userRequestUpdate) {
        UserResponse updateUser = userService.updateUser(id, userRequestUpdate);
        return ResponseEntity.ok(updateUser);
    }

    @PatchMapping("/passwordChange/{id}")
    public  ResponseEntity<Void> changePassword(@PathVariable(name = "id") Long id, @RequestBody PasswordUpdate passwordUpdate){
        userService.updatePassword(id, passwordUpdate.getOldPassword(), passwordUpdate.getNewPassword());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password-recovery")
    public ResponseEntity<String> passwordRecovery(@RequestBody EmailDto emailDto) {
        codeVerificationService.sendCodeVerification(emailDto);
        return ResponseEntity.ok("Se ha enviado el código de recuperación.");
    }

    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(@RequestBody CodeVerificationDto codeVerificationDto) {
        codeVerificationService.verifyCode(codeVerificationDto);
        return ResponseEntity.ok("Código verificado, puede proceder a cambiar la contraseña.");
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordCodeDto changePasswordCodeDto) {
        codeVerificationService.changePassword(changePasswordCodeDto.getEmail(), changePasswordCodeDto.getNewPassword());
        return ResponseEntity.ok("Contraseña cambiada exitosamente.");
    }
}
