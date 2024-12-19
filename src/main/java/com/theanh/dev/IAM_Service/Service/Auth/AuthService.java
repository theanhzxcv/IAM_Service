package com.theanh.dev.IAM_Service.Service.Auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theanh.dev.IAM_Service.Dtos.Auth.AuthDto;
import com.theanh.dev.IAM_Service.Dtos.User.UserDto;
import com.theanh.dev.IAM_Service.Exception.AppException;
import com.theanh.dev.IAM_Service.Exception.ErrorCode;
import com.theanh.dev.IAM_Service.Mapper.UserMapper;
import com.theanh.dev.IAM_Service.Model.Users;
import com.theanh.dev.IAM_Service.Repository.UserRepository;
import com.theanh.dev.IAM_Service.Response.AuthResponse;
import com.theanh.dev.IAM_Service.Security.JwtUtil;
import com.theanh.dev.IAM_Service.Service.Email.EmailService;
import com.theanh.dev.IAM_Service.Service.Email.OtpUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService implements IAuthService{

    UserRepository userRepository;

    UserMapper userMapper;

    PasswordEncoder passwordEncoder;

    JwtUtil jwtUtil;

    OtpUtil otpUtil;

    EmailService emailService;

    @Override
    public AuthResponse login(AuthDto authDto) {
        Users user = userRepository.findByEmail(authDto.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean authenticated = passwordEncoder.matches(authDto.getPassword(), user.getPassword());

        if (!authenticated)
            throw new AppException(ErrorCode.UNAUTHENTICATED);

//        try {
//            emailService.sendOtpEmail(authDto.getEmail(), otpUtil.generateOtp());
//        } catch (MessagingException e) {
//            throw new RuntimeException(e);
//        }

        String accessToken;

        String refreshToken;

        try {
            accessToken = jwtUtil.generateAccessToken(user);
            refreshToken = jwtUtil.generateRefreshToken(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .authenticated(true)
                .build();
    }

    @Override
    public String register(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        if (userDto.getEmail().isEmpty() || userDto.getPassword().isEmpty()){
            throw new AppException(ErrorCode.INCOMPLETE_DETAIL);
        }

        Users register = userMapper.toUser(userDto);
        register.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Users saveUser = userRepository.save(register);

        emailService.sendRegistrationEmail(userDto.getEmail(), userDto.getPassword(), userDto.getFirstname(), userDto.getLastname());

        return "Registered successfully!";
    }

    @Override
    public AuthResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        String accessToken = "";
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            throw new RuntimeException("...");

        refreshToken = authHeader.substring(7);
        userEmail = jwtUtil.extractEmail(refreshToken);

        if (userEmail != null) {
            var user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            if (jwtUtil.isTokenValid(refreshToken, user)) {
                try {
                    accessToken = jwtUtil.generateAccessToken(user);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .authenticated(true)
                .build();
    }

//    @Override
//    public AuthResponse verifyAccount(String email, String otp) {
//        Users user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
//
//        if (user.getOtp().equals(otp) && Duration.between(user.getOtpGeneratedTime(),
//                LocalDateTime.now()).getSeconds() < (1 * 60)) {
//            user.setActive(true);
//            userRepository.save(user);
//            return "OTP verified you can login";
//        }
//        return "Please regenerate otp and try again";
//    }
}
