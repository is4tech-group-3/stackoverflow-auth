package com.stackoverflow.util;

import com.stackoverflow.repository.UserRepository;

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

    public static void validatePassword(String password) {
        String passwordRegex = "^(?=\\w*\\d)(?=\\w*[A-Z])(?=\\w*[a-z])\\S{8,16}$";
        if (!password.matches(passwordRegex)) {
            throw new IllegalArgumentException(
                    "The password must be between 8 and 16 characters, at least one digit, at least one lower case letter and at least one upper case letter.");
        }
    }

    public static void validateUsername(String username) {
        String usernameRegex = "^[a-zA-Z0-9_]{3,15}$";
        if (!username.matches(usernameRegex)) {
            throw new IllegalArgumentException(
                    "The username must be no longer than 15 characters, only letters, numbers and underscores are allowed, and no spaces");
        }
    }

    public static void validateMaxLength(String value, int maxLength, String fieldName) {
        if (value != null && value.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " must not be longer than " + maxLength + " characters");
        }
    }
}
