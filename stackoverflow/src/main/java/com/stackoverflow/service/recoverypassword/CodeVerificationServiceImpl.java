package com.stackoverflow.service.recoverypassword;

import com.stackoverflow.bo.CodeVerification;
import com.stackoverflow.bo.User;
import com.stackoverflow.dto.CodeVerificationDto;
import com.stackoverflow.dto.EmailDto;
import com.stackoverflow.repository.CodeVerificationRepository;
import com.stackoverflow.repository.UserRepository;
import com.stackoverflow.service.MailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.webjars.NotFoundException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CodeVerificationServiceImpl implements CodeVerificationService{
    @Autowired
    private CodeVerificationRepository codeVerificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailService mailService;

    private final PasswordEncoder passwordEncoder;

    public String generateCode(){
        String numbers = "0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();
        for(int i=0; i<4; i++){
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
    public void verifyCode(CodeVerificationDto codeVerificationDto) {
        CodeVerification codeVerification = codeVerificationRepository
                .findByEmailAndCode(codeVerificationDto.getEmail(), codeVerificationDto.getCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código no válido"));

        if (codeVerification.getDateExpiration().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El código ha expirado");
        }
    }

    @Override
    @Transactional
    public void changePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        codeVerificationRepository.deleteByEmail(email);
    }
}
