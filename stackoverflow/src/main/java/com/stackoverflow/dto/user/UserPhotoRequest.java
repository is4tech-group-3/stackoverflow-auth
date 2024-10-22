package com.stackoverflow.dto.user;

import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
@NoArgsConstructor
public class UserPhotoRequest {
    private MultipartFile image;
}
