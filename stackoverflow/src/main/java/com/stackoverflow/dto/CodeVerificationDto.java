package com.stackoverflow.dto;

import lombok.Data;

@Data
public class CodeVerificationDto {
    private String email;
    private String code;
}
