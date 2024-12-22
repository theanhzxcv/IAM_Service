package com.theanh.dev.IAM_Service.Services.Auth;

import com.theanh.dev.IAM_Service.Dtos.Auth.AuthDto;
import com.theanh.dev.IAM_Service.Dtos.Auth.VerificationDto;
import com.theanh.dev.IAM_Service.Dtos.User.UserDto;
import com.theanh.dev.IAM_Service.Exception.AppException;
import com.theanh.dev.IAM_Service.Exception.ErrorCode;
import com.theanh.dev.IAM_Service.Mapper.UserMapper;
import com.theanh.dev.IAM_Service.Models.UserActivity;
import com.theanh.dev.IAM_Service.Models.Users;
import com.theanh.dev.IAM_Service.Repositories.UserActivityRepository;
import com.theanh.dev.IAM_Service.Repositories.UserRepository;
import com.theanh.dev.IAM_Service.Response.AuthResponse;
import com.theanh.dev.IAM_Service.Security.JwtUtil;
import com.theanh.dev.IAM_Service.Services.Email.EmailService;
import com.theanh.dev.IAM_Service.Services.Tfa.TwoFactorAuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService implements IAuthService{
    JwtUtil jwtUtil;
    UserMapper userMapper;
    EmailService emailService;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    UserActivityRepository userActivityRepository;
    TwoFactorAuthenticationService tfaService;

    @Override
    public String login(AuthDto authDto) {
        Users user = userRepository.findByEmail(authDto.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED_USER));

        if (!passwordEncoder.matches(authDto.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        try {
            UserActivity userActivity = UserActivity
                    .builder()
                    .email(user.getEmail())
                    .activity("Log in")
                    .timestamp(new Date())
                    .build();
            userActivityRepository.save(userActivity);
            return "Log in successfully. Please verify to continue!";
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    @Override
    public AuthResponse verifyAccount(VerificationDto verificationDto) {
        Users user = userRepository.findByEmail(verificationDto.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED_USER));

        if (!tfaService.verifyCode(user.getSecret(), verificationDto.getOtp())) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        try {
            String accessToken = jwtUtil.generateAccessToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .authenticated(true)
                    .build();
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    @Override
    public String register(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.EXISTED_USER);
        }

        if (userDto.getEmail() == null || userDto.getPassword() == null){
            throw new AppException(ErrorCode.FIELD_REQUIRED);
        }

        try {
            Users register = userMapper.toUser(userDto);
            register.setPassword(passwordEncoder.encode(userDto.getPassword()));
            register.setSecret(tfaService.generateSecretKey());
            UserActivity userActivity = UserActivity
                    .builder()
                    .email(register.getEmail())
                    .activity("Log in")
                    .timestamp(new Date())
                    .build();
            userActivityRepository.save(userActivity);
            userRepository.save(register);
            emailService.sendRegistrationEmail(userDto.getEmail(), userDto.getPassword(), userDto.getFirstname(), userDto.getLastname());
            return "Register successfully!";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("...");
        }

        final String refreshToken = authHeader.substring(7);
        final String userEmail;
        try {
            userEmail = jwtUtil.extractEmail(refreshToken);
        } catch (Exception e) {
            throw new RuntimeException("...");
        }

        Users user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED_USER));

        if (!jwtUtil.isTokenValid(refreshToken, user)) {
            throw new RuntimeException("Token invalid");
        }

        try {
            String accessToken = jwtUtil.generateAccessToken(user);
            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .authenticated(true)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error happened, please try again.");
        }
    }

}
