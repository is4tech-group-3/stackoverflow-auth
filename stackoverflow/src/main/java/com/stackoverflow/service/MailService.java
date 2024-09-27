package com.stackoverflow.service;

import com.stackoverflow.util.LoggerUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendMail(String to, String subject, String body){
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(body, true);

            javaMailSender.send(message);

            logger.info("Sending email to: {}", to);
            logger.info("Email subject: {}", subject);
            logger.info("Email body: {}", body);
        } catch (Exception e) {
            logger.error("Error al enviar el correo");
        }
    }
}
