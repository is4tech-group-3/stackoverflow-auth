package com.stackoverflow.dto.codeverification;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordResetDto {
    private String email;
    private String code;
    private String newPassword;
}
