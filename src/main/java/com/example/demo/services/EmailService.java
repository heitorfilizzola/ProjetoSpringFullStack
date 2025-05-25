package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String fromEmail;

    public void sendPasswordResetEmail(String to, String subject, String token, String resetUrlBase) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);

            String resetUrl = resetUrlBase + "/reset-password?token=" + token;
            String emailText = "Olá!\n\n"
                    + "Você solicitou a redefinição da sua senha.\n"
                    + "Clique no link abaixo para definir uma nova senha:\n"
                    + resetUrl + "\n\n"
                    + "Se você não solicitou esta redefinição, por favor, ignore este e-mail.\n\n"
                    + "O link expirará em 1 hora.\n\n"
                    + "Atenciosamente,\nEquipe NoteSync";

            message.setText(emailText);
            mailSender.send(message);
            System.out.println("E-mail de redefinição de senha enviado para: " + to);
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail de redefinição de senha para " + to + ": " + e.getMessage());
        }
    }
}