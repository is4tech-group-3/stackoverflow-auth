package com.stackoverflow.dto;

import lombok.Data;

@Data
public class ChangePasswordCodeDto {
    private String email;
    private String newPassword;
}
