package br.ufscar.dc.dsw.imobiliaria.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String text) {
        if (mailSender == null) {
            System.out.println("MailSender not configured — skipping email to " + to);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            // log and ignore; best-effort notification
            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
        }
    }
}
