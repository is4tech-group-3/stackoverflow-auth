package com.stackoverflow.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.Map;

@AllArgsConstructor
@Service
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender javaMailSender;
    private final FreeMarkerConfigurer freeMarkerConfigurer;

    public void sendEmailWelcome(String to, String password, String nameUser){
        String subject = "¡Bienvenido a la familia BUGSTACK! 🥳";
        String body = generateBody("template-welcome.html", Map.of("nameUser", nameUser, "password", password));
        sendMail(to, subject, body);
    }

    public void sendEmailPasswordReset(String to, String nameUser, String code){
        String subject = "Código de verificación";
        String body = generateBody("template-password.html", Map.of("code", code, "nameUser", nameUser));
        sendMail(to, subject, body);
    }

    private String generateBody(String templateName, Map<String, Object> model) {
        try {
            freemarker.template.Template template = freeMarkerConfigurer.getConfiguration().getTemplate(templateName);
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (Exception e) {
            logger.error("Error al procesar la plantilla: {}", e.getMessage());
            return "";
        }
    }

    public void sendMail(String to, String subject, String body){
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(body, true);

            ClassPathResource logoResource = new ClassPathResource("static/images/Logo.png");
            messageHelper.addInline("Logo", logoResource);
            ClassPathResource logoBlackResource = new ClassPathResource("static/images/LogoBlack.png");
            messageHelper.addInline("LogoBlack", logoBlackResource);
            ClassPathResource fondoBugsStackResource = new ClassPathResource("static/images/Fondo-Bugstack.png");
            messageHelper.addInline("Fondo-Bugstack", fondoBugsStackResource);

            javaMailSender.send(message);

            logger.info("Sending email to: {}", to);
            logger.info("Email subject: {}", subject);
        } catch (Exception e) {
            logger.error("Error al enviar el correo", e);
        }
    }
}
