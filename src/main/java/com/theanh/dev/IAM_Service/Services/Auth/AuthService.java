package com.theanh.dev.IAM_Service.Services.Auth;

import com.theanh.dev.IAM_Service.Dtos.Requests.Auth.LoginRequest;
import com.theanh.dev.IAM_Service.Dtos.Requests.Auth.RegistrationRequest;
import com.theanh.dev.IAM_Service.Dtos.Requests.Auth.VerificationRequest;
import com.theanh.dev.IAM_Service.Exception.AppException;
import com.theanh.dev.IAM_Service.Exception.ErrorCode;
import com.theanh.dev.IAM_Service.Mapper.UserMapper;
import com.theanh.dev.IAM_Service.Models.Roles;
import com.theanh.dev.IAM_Service.Models.UserActivity;
import com.theanh.dev.IAM_Service.Models.Users;
import com.theanh.dev.IAM_Service.Repositories.RoleRepository;
import com.theanh.dev.IAM_Service.Repositories.UserActivityRepository;
import com.theanh.dev.IAM_Service.Repositories.UserRepository;
import com.theanh.dev.IAM_Service.Dtos.Response.Auth.AuthResponse;
import com.theanh.dev.IAM_Service.Jwt.JwtUtil;
import com.theanh.dev.IAM_Service.Services.Email.EmailService;
import com.theanh.dev.IAM_Service.Services.ServiceImp.IAuthService;
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
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService implements IAuthService {
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserActivityRepository userActivityRepository;
    private final TwoFactorAuthenticationService tfaService;

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
    public String login(LoginRequest loginRequest) {
        Users user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED_USER));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        try {
            user.setVerified(false);
            userRepository.save(user);
            return "Log in successfully. Please verify to continue!";
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    @Override
    public AuthResponse verifyAccount(VerificationRequest verificationRequest) {
        Users user = userRepository.findByEmail(verificationRequest.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED_USER));

        if (!tfaService.verifyCode(user.getSecret(), verificationRequest.getOtp())) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        if (user.isVerified()) {
            throw new RuntimeException("This account has been verified");
        }

        try {
            String accessToken = jwtUtil.generateAccessToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);

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
    public String register(RegistrationRequest registrationRequest, HttpServletRequest request) {
        if (userRepository.findByEmail(registrationRequest.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.EXISTED_USER);
        }

        if (registrationRequest.getEmail() == null || registrationRequest.getPassword() == null) {
            throw new AppException(ErrorCode.FIELD_REQUIRED);
        }

        try {
            Users register = userMapper.toUser(registrationRequest);
            register.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            register.setSecret(tfaService.generateSecretKey());

            String clientIp = getClientIp(request);
            if ("0:0:0:0:0:0:0:1".equals(clientIp)) {
                clientIp = "127.0.0.1";
            }
            UserActivity userActivity = UserActivity
                    .builder()
                    .ip(clientIp)
                    .email(register.getEmail())
                    .activity("Register")
                    .timestamp(new Date())
                    .build();

            userActivityRepository.save(userActivity);
            Set<Roles> roles = new HashSet<>();
            var userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("Role user nor found"));
            roles.add(userRole);
            register.setRoles(roles);
            userRepository.save(register);
            emailService.sendRegistrationEmail(registrationRequest.getEmail(),
                    registrationRequest.getPassword(),
                    registrationRequest.getFirstname(),
                    registrationRequest.getLastname());
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
