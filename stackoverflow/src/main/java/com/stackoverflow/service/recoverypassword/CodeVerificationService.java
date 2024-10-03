package com.stackoverflow.service.recoverypassword;

import com.stackoverflow.dto.CodeVerificationDto;
import com.stackoverflow.dto.EmailDto;

public interface CodeVerificationService {
    void sendCodeVerification(EmailDto emailDto);
    void verifyCode(CodeVerificationDto codeVerificationDto);
    void changePassword(String email, String newPassword);
}
