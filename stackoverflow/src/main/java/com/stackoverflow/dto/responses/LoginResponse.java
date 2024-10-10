package com.stackoverflow.dto.responses;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private long expiresIn;
    private List<String> roles;
    private Long userId;
}
