package com.stackoverflow.dto.user;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserResponse {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String username;
    private Boolean status;
}
