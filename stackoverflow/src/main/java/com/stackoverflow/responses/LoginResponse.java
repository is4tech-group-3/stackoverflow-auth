package com.stackoverflow.responses;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private long expiresIn;
}
