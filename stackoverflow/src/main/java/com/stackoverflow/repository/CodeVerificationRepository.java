package com.stackoverflow.repository;

import com.stackoverflow.bo.CodeVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CodeVerificationRepository extends JpaRepository<CodeVerification, Long> {
    Optional<CodeVerification> findByEmailAndCode(String email, String code);
    void deleteByEmail(String email);
}
