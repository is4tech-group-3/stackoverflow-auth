package com.stackoverflow.util;

import com.stackoverflow.bo.User;
import com.stackoverflow.dto.user.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserConvert {
    public UserResponse UserToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .username(user.getUsername())
                .idProfile(user.getProfileId())
                .status(user.getStatus())
                .build();
    }
}
