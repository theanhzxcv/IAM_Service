package com.theanh.dev.IAM_Service.Service.Email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {

    JavaMailSender javaMailSender;

    public void sendRegistrationEmail(String toEmail, String firstname, String lastname) throws MessagingException {

        String subject = "Welcome to IAM Service!";
        String text = "Hello " + lastname + " " + firstname + ",\n\nThank you for registering with us! We're excited to have you on board.";

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
        messageHelper.setTo(toEmail);
        messageHelper.setSubject(subject);
        messageHelper.setText(text, true);

        javaMailSender.send(mimeMessage);
    }
}
