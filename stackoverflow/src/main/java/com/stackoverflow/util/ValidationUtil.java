package com.stackoverflow.util;

import com.stackoverflow.repository.user.UserRepository;

public class ValidationUtil {

    private ValidationUtil() { 

    }

    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
    }

    public static void validationEmailFormat(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!email.matches(emailRegex)) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    public static void validateUniqueEmail(UserRepository userRepository, String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }
    }

    public static void validatePassword(String password){
        String passwordRegex = "^(?=\\w*\\d)(?=\\w*[A-Z])(?=\\w*[a-z])\\S{8,16}$";
        if(!password.matches(passwordRegex)){
            throw new IllegalArgumentException("The password must be between 8 and 16 characters, at least one digit, at least one lower case letter and at least one upper case letter.");
        }
    }
}
