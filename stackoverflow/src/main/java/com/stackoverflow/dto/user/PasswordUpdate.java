package com.stackoverflow.dto.user;

import lombok.Data;

@Data
public class PasswordUpdate {
    private String oldPassword;
    private String newPassword;
}
