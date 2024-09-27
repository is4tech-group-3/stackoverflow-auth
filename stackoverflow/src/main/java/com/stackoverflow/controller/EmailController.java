package com.stackoverflow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.mail.internet.MimeMessage;

@RestController
@RequestMapping("/api/v1/auth")
public class EmailController {

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/forgotPassword")
    public ResponseEntity<?> emailForgotPassword() {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo("lesterlopez.559@gmail.com");
            helper.setFrom("lopezgalvezlester@gmail.com");
            helper.setSubject("Código de Recuperación de Contraseña");

            String htmlContent = "<!DOCTYPE html>" +
                    "<html lang=\"es\">" +
                    "<head>" +
                    "    <meta charset=\"UTF-8\">" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                    "    <style>" +
                    "        body {" +
                    "            font-family: Arial, sans-serif;" +
                    "            color: #333;" +
                    "            margin: 0;" +
                    "            padding: 0;" +
                    "            background-color: #f4f4f4;" +
                    "        }" +
                    "        .container {" +
                    "            width: 100%;" +
                    "            max-width: 600px;" +
                    "            margin: 0 auto;" +
                    "            background-color: #ffffff;" +
                    "            padding: 20px;" +
                    "            border: 1px solid #dddddd;" +
                    "            border-radius: 5px;" +
                    "            box-shadow: 0 2px 4px rgba(0,0,0,0.1);" +
                    "        }" +
                    "        .header {" +
                    "            text-align: center;" +
                    "            border-bottom: 2px solid #007bff;" +
                    "            padding-bottom: 10px;" +
                    "            margin-bottom: 20px;" +
                    "        }" +
                    "        .header h1 {" +
                    "            margin: 0;" +
                    "            color: #007bff;" +
                    "        }" +
                    "        .content {" +
                    "            margin: 20px 0;" +
                    "        }" +
                    "        .content p {" +
                    "            margin: 0 0 10px;" +
                    "        }" +
                    "        .code {" +
                    "            display: block;" +
                    "            padding: 10px;" +
                    "            color: #ffffff;" +
                    "            background-color: #007bff;" +
                    "            text-align: center;" +
                    "            border-radius: 5px;" +
                    "            font-weight: bold;" +
                    "            margin: 20px 0;" +
                    "        }" +
                    "        .footer {" +
                    "            text-align: center;" +
                    "            margin-top: 20px;" +
                    "            font-size: 0.9em;" +
                    "            color: #777;" +
                    "        }" +
                    "    </style>" +
                    "</head>" +
                    "<body>" +
                    "    <div class=\"container\">" +
                    "        <div class=\"header\">" +
                    "            <h1>Stackbugs</h1>" +
                    "        </div>" +
                    "        <div class=\"content\">" +
                    "            <p>Hola Ejemplo de nombre de usuario,</p>" +
                    "            <p>Hemos recibido una solicitud para recuperar la contraseña de tu cuenta en Stackbugs. Para completar el proceso, utiliza el siguiente código:</p>" +
                    "            <span class=\"code\">Ejemplo de codigo</span>" +
                    "            <p>Ingresa este código en la página de recuperación para restablecer tu contraseña. Si no solicitaste esta recuperación, por favor ignora este correo.</p>" +
                    "            <p>Si necesitas asistencia adicional, no dudes en contactarnos.</p>" +
                    "        </div>" +
                    "        <div class=\"footer\">" +
                    "            Gracias,<br>" +
                    "            El equipo de Stackbugs" +
                    "        </div>" +
                    "    </div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);

            return new ResponseEntity<>("Correo de recuperación enviado.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al enviar el correo.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/changePassword")
    public ResponseEntity<?> emailChangePassword() {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo("lesterlopez.559@gmail.com");
            helper.setFrom("lopezgalvezlester@gmail.com");
            helper.setSubject("Confirmación para Cambiar tu Contraseña");

            String htmlContent = "<!DOCTYPE html>" +
                    "<html lang=\"es\">" +
                    "<head>" +
                    "    <meta charset=\"UTF-8\">" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                    "    <style>" +
                    "        body {" +
                    "            font-family: Arial, sans-serif;" +
                    "            color: #333;" +
                    "            margin: 0;" +
                    "            padding: 0;" +
                    "            background-color: #f4f4f4;" +
                    "        }" +
                    "        .container {" +
                    "            width: 100%;" +
                    "            max-width: 600px;" +
                    "            margin: 0 auto;" +
                    "            background-color: #ffffff;" +
                    "            padding: 20px;" +
                    "            border: 1px solid #dddddd;" +
                    "            border-radius: 5px;" +
                    "            box-shadow: 0 2px 4px rgba(0,0,0,0.1);" +
                    "        }" +
                    "        .header {" +
                    "            text-align: center;" +
                    "            border-bottom: 2px solid #007bff;" +
                    "            padding-bottom: 10px;" +
                    "            margin-bottom: 20px;" +
                    "        }" +
                    "        .header h1 {" +
                    "            margin: 0;" +
                    "            color: #007bff;" +
                    "        }" +
                    "        .content {" +
                    "            margin: 20px 0;" +
                    "        }" +
                    "        .content p {" +
                    "            margin: 0 0 10px;" +
                    "        }" +
                    "        .button {" +
                    "            display: inline-block;" +
                    "            padding: 10px 20px;" +
                    "            color: #ffffff;" +
                    "            background-color: #007bff;" +
                    "            text-decoration: none;" +
                    "            border-radius: 5px;" +
                    "            font-weight: bold;" +
                    "            margin: 20px 0;" +
                    "        }" +
                    "        .footer {" +
                    "            text-align: center;" +
                    "            margin-top: 20px;" +
                    "            font-size: 0.9em;" +
                    "            color: #777;" +
                    "        }" +
                    "    </style>" +
                    "</head>" +
                    "<body>" +
                    "    <div class=\"container\">" +
                    "        <div class=\"header\">" +
                    "            <h1>Stackbugs</h1>" +
                    "        </div>" +
                    "        <div class=\"content\">" +
                    "            <p>Hola Ejermplo de nombre de usuario,</p>" +
                    "            <p>Hemos recibido una solicitud para cambiar la contraseña de tu cuenta en Stackbugs. Para confirmar que deseas proceder con este cambio, por favor, haz clic en el siguiente enlace:</p>" +
                    "            <a href=\"https://tuapp.com/confirmChangePassword?token=" + "\" class=\"button\">Confirmar Cambio de Contraseña</a>" +
                    "            <p>Si no solicitaste el cambio de contraseña, por favor ignora este correo. Tu contraseña no se cambiará.</p>" +
                    "            <p>Si necesitas asistencia adicional, no dudes en contactarnos.</p>" +
                    "        </div>" +
                    "        <div class=\"footer\">" +
                    "            Gracias,<br>" +
                    "            El equipo de Stackbugs" +
                    "        </div>" +
                    "    </div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);

            return new ResponseEntity<>("Correo de confirmación enviado.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al enviar el correo.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
