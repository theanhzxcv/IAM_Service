package com.theanh.dev.IAM_Service.Service.Email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {

    JavaMailSender mailSender;

    public void sendRegistrationEmail(String email, String password, String firstname, String lastname) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Register successful!!");
        message.setText("Thank " + lastname + " " + firstname + "registering! You can now log in using your credentials."
        + "\nYour email: " + email
        + "\nYour password: " + password);

        mailSender.send(message);
    }

    public void sendVerifyOtpEmail(String email) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Verify OTP");
        mimeMessageHelper.setText("""
        <div>
          <a href="http://localhost:8081/iams/auth/verify-account?email=%s" target="_blank">Click link to verify</a>
        </div>
        """.formatted(email), true);

        mailSender.send(message);
    }

    public void sendResetPasswordEmail(String email, String token) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Forgot password ?");
        mimeMessageHelper.setText("""
        <div>
          <a href="http://localhost:8081/iam_service/users/reset-password?email=%s&token=%s" target="_blank">Click link to reset your password.</a>
        </div>
        """.formatted(email, token), true);

        mailSender.send(message);
    }

    public void sendPasswordChangeEmail(String toEmail, String newPasword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Password Changed Successfully");
        message.setText("Your password has been successfully changed. " +
                "\nIf you did not make this change, please contact support immediately." +
                "\nYour new password: " + newPasword);

        mailSender.send(message);
    }

    public void sendProfileUpdateEmail(String toEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Profile Updated Successfully");
        message.setText("Your profile has been updated successfully. " +
                "\nIf you did not make this change, please contact support immediately.");

        mailSender.send(message);
    }
}
