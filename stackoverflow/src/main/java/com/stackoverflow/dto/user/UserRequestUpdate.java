package com.stackoverflow.dto.user;

import lombok.Data;

@Data
public class UserRequestUpdate {
    private String name;
    private String surname;
    private String username;
    private String photoProfile;
}
    