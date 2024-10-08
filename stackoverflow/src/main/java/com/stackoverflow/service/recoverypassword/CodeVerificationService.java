package com.stackoverflow.service.recoverypassword;

import com.stackoverflow.dto.codeverification.EmailDto;
import com.stackoverflow.dto.codeverification.PasswordResetDto;

public interface CodeVerificationService {
    void sendCodeVerification(EmailDto email);  
    void verifyCodeAndChangePassword(PasswordResetDto passwordResetDto);
}
