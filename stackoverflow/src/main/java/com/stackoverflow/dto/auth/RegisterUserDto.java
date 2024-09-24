package com.stackoverflow.dto.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class RegisterUserDto {
    private String name;
    private String surname;
    private String email;
    private String username;
    private String password;
    private Boolean status;
}
