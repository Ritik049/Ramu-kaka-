package com.backendExtension.ramukaka.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(List<String> to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("ritikagrawal047@gmail.com"); // replace with your email
            helper.setTo(to.toArray(new String[0]));
            helper.setSubject(subject);
            helper.setText(body.replace("\n", "<br>"), true); // HTML body

            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


