package com.theanh.dev.IAM_Service.Services.Auth;

import com.theanh.dev.IAM_Service.Dtos.Auth.LoginDto;
import com.theanh.dev.IAM_Service.Dtos.Auth.RegistrationDto;
import com.theanh.dev.IAM_Service.Dtos.Auth.VerificationDto;
import com.theanh.dev.IAM_Service.Exception.AppException;
import com.theanh.dev.IAM_Service.Exception.ErrorCode;
import com.theanh.dev.IAM_Service.Mapper.UserMapper;
import com.theanh.dev.IAM_Service.Models.UserActivity;
import com.theanh.dev.IAM_Service.Models.Users;
import com.theanh.dev.IAM_Service.Repositories.RoleRepository;
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
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService implements IAuthService {
    JwtUtil jwtUtil;
    UserMapper userMapper;
    EmailService emailService;
    UserRepository userRepository;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;
    UserActivityRepository userActivityRepository;
    TwoFactorAuthenticationService tfaService;

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    @Override
    public String login(LoginDto loginDto) {
        Users user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED_USER));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

//        if (!user.is2faEnable()) {
//
//        }

        try {

            user.setVerified(false);
            userRepository.save(user);
            return "Log in successfully. Please verify to continue!";
//            String accessToken = jwtUtil.generateAccessToken(user);
//            String refreshToken = jwtUtil.generateRefreshToken(user);
//
//            return AuthResponse.builder()
//                    .accessToken(accessToken)
//                    .refreshToken(refreshToken)
//                    .authenticated(true)
//                    .build();
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }


    }

    @Override
    public AuthResponse verifyAccount(VerificationDto verificationDto, HttpServletRequest request) {
        Users user = userRepository.findByEmail(verificationDto.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED_USER));

        if (!tfaService.verifyCode(user.getSecret(), verificationDto.getOtp())) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        if (user.isVerified()) {
            throw new RuntimeException("This account has been verified");
        }

        try {
            String accessToken = jwtUtil.generateAccessToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);
            String clientIp = getClientIp(request);
            if ("0:0:0:0:0:0:0:1".equals(clientIp)) {
                clientIp = "127.0.0.1";
            }
            UserActivity userActivity = UserActivity
                    .builder()
                    .ip(clientIp)
                    .email(user.getEmail())
                    .activity("Log in")
                    .timestamp(new Date())
                    .build();
            userActivityRepository.save(userActivity);

            user.setVerified(true);
            userRepository.save(user);
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
    public String register(RegistrationDto registrationDto) {
        if (userRepository.findByEmail(registrationDto.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.EXISTED_USER);
        }

        if (registrationDto.getEmail() == null || registrationDto.getPassword() == null) {
            throw new AppException(ErrorCode.FIELD_REQUIRED);
        }

        try {
            Users register = userMapper.toUser(registrationDto);
            register.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
            register.setSecret(tfaService.generateSecretKey());

//            String clientIp = getClientIp(request);
//            if ("0:0:0:0:0:0:0:1".equals(clientIp)) {
//                clientIp = "127.0.0.1";
//            }
//            UserActivity userActivity = UserActivity
//                    .builder()
//                    .ip(clientIp)
//                    .email(user.getEmail())
//                    .activity("Log in")
//                    .timestamp(new Date())
//                    .build();
//            userActivityRepository.save(userActivity);
//            var roles = roleRepository.findAllById(userDto.getRoles());
//            register.setRoles("USER");
            userRepository.save(register);
            emailService.sendRegistrationEmail(registrationDto.getEmail(), registrationDto.getPassword(), registrationDto.getFirstname(), registrationDto.getLastname());
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

    public void logout(HttpServletRequest request) {

    }

}
