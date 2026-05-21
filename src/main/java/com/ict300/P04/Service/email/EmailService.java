package com.ict300.P04.Service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void envoyerEmailHtml(String destinataire, String sujet, String contenuHtml) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper  helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(destinataire);
        helper.setSubject(sujet);


        helper.setText(contenuHtml, true);

        mailSender.send(message);
    }
}
