package com.stackoverflow.dto.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class LoginUserDto {
    private String email;
    private String password;
}
