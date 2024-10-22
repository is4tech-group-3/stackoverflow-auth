package com.stackoverflow.service.recoverypassword;

import com.stackoverflow.bo.CodeVerification;
import com.stackoverflow.bo.User;
import com.stackoverflow.dto.codeverification.EmailDto;
import com.stackoverflow.dto.codeverification.PasswordResetDto;
import com.stackoverflow.repository.CodeVerificationRepository;
import com.stackoverflow.repository.UserRepository;
import com.stackoverflow.service.MailService;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CodeVerificationServiceImpl implements CodeVerificationService {
    private final CodeVerificationRepository codeVerificationRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    private final Validator validator;

    private final PasswordEncoder passwordEncoder;

    public String generateCode() {
        String numbers = "0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            code.append(numbers.charAt(random.nextInt(numbers.length())));
        }
        return code.toString();
    }

    @Override
    @Transactional
    public void sendCodeVerification(EmailDto emailDto) {
        String email = emailDto.getEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        codeVerificationRepository.deleteByEmail(email);

        String code = generateCode();

        CodeVerification codeVerification = new CodeVerification();
        codeVerification.setEmail(email);
        codeVerification.setCode(code);
        codeVerification.setDateExpiration(LocalDateTime.now().plusMinutes(10));

        codeVerificationRepository.save(codeVerification);

        mailService.sendEmailPasswordReset(email, user.getName(), code);
    }

    @Override
    @Transactional
    public void verifyCodeAndChangePassword(PasswordResetDto passwordResetDto) {
        CodeVerification codeVerification = codeVerificationRepository
                .findByEmailAndCode(passwordResetDto.getEmail(), passwordResetDto.getCode())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid code or email not found"));

        if (codeVerification.getDateExpiration().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code expired");
        }

        User user = userRepository.findByEmail(passwordResetDto.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String regex = "^(?=.*\\d)(?=.*[\\u0021-\\u002b\\u003c-\\u0040])(?=.*[A-Z])(?=.*[a-z])\\S{8,16}$";
        if (!passwordResetDto.getNewPassword().matches(regex)) {
            throw new IllegalArgumentException("New password does not meet security requirements");
        }

        user.setPassword(passwordEncoder.encode(passwordResetDto.getNewPassword()));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty())
            throw new ConstraintViolationException(violations);

        userRepository.save(user);

        codeVerificationRepository.deleteByEmail(passwordResetDto.getEmail());
    }
}
