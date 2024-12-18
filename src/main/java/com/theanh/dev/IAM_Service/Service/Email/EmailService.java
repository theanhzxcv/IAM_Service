package com.theanh.dev.IAM_Service.Service.Email;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {

    JavaMailSender mailSender;

    public void sendRegistrationEmail(String toEmail, String password, String firstname, String lastname) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Registration Successful");
        message.setText("Thank " + lastname + " " + firstname + " for registering!"
                + "\nYour email: " + toEmail
                + "\nYour password: " + password
                + "\nYou can now log in using your credentials.");

        mailSender.send(message);
    }

    public void sendPasswordChangeEmail(String toEmail, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Password Changed Successfully");
        message.setText("Your password has been changed successfully. " +
                "If you did not make this change, please contact support immediately.");
        message.setText("Your new password: " + newPassword);

        mailSender.send(message);
    }

    public void sendProfileUpdateEmail(String toEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Profile Updated Successfully");
        message.setText("Your profile has been updated successfully. " +
                "If you did not make this change, please contact support immediately.");

        mailSender.send(message);
    }
}
