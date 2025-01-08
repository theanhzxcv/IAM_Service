package com.theanh.dev.IAM_Service.Services.ServiceImp.Auth;

import com.theanh.dev.IAM_Service.Dtos.Requests.Auth.LoginRequest;
import com.theanh.dev.IAM_Service.Dtos.Requests.Auth.LogoutRequest;
import com.theanh.dev.IAM_Service.Dtos.Requests.Auth.RegistrationRequest;
import com.theanh.dev.IAM_Service.Dtos.Response.Auth.AuthResponse;
import com.theanh.dev.IAM_Service.Exception.AppException;
import com.theanh.dev.IAM_Service.Exception.ErrorCode;
import com.theanh.dev.IAM_Service.Jwt.JwtUtil;
import com.theanh.dev.IAM_Service.Mapper.UserMapper;
import com.theanh.dev.IAM_Service.Models.Roles;
import com.theanh.dev.IAM_Service.Models.Users;
import com.theanh.dev.IAM_Service.Repositories.RoleRepository;
import com.theanh.dev.IAM_Service.Repositories.UserRepository;
import com.theanh.dev.IAM_Service.Services.ServiceImp.Blacklist.JwtBlacklistService;
import com.theanh.dev.IAM_Service.Services.ServiceImp.Email.EmailService;
import com.theanh.dev.IAM_Service.Services.IAuthService;
import com.theanh.dev.IAM_Service.Services.ServiceImp.Tfa.TwoFactorAuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class KeycloakAuthService implements IAuthService {
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtBlacklistService jwtBlacklistService;
    private final TwoFactorAuthenticationService tfaService;

    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;
    @Value("${keycloak.client.id}")
    private String clientId;
    @Value("${keycloak.client.secret}")
    private String clientSecret;

    private String getAdminToken() {
        String adminUsername = "theanh";
        String adminPassword = "theanh";
        String tokenUrl = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("username", adminUsername);
        body.add("password", adminPassword);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(
                tokenUrl,
                new org.springframework.http.HttpEntity<>(body, headers),
                Map.class
        );

        return (String) response.getBody().get("access_token");
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        String loginUrl = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        if (userRepository.findByEmail(loginRequest.getEmail()).isEmpty()) {
            throw new AppException(ErrorCode.NOT_EXISTED_USER);

        }
        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "password");
        body.put("client_id", clientId);
        body.put("client_secret", clientSecret);
        body.put("username", loginRequest.getEmail());
        body.put("password", loginRequest.getPassword());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String formBody = body.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((a, b) -> a + "&" + b)
                .orElse("");

        HttpEntity<String> requestEntity = new HttpEntity<>(formBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<Map> response = restTemplate.exchange(loginUrl, HttpMethod.POST, requestEntity, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (response == null
                    || !responseBody.containsKey("access_token")
                    || !responseBody.containsKey("refresh_token")) {
                throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
            }

            if (response.getStatusCode().is2xxSuccessful()) {
                String accessToken = (String) responseBody.get("access_token");
                String refreshToken = (String) responseBody.get("refresh_token");

                return AuthResponse
                        .builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .authenticated(true)
                        .build();
            }

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 401) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }
        }
        throw new AppException(ErrorCode.UNAUTHENTICATED);
    }

    @Override
    public String register(RegistrationRequest registrationRequest) {
        if (userRepository.findByEmail(registrationRequest.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.EXISTED_USER);
        }

        String adminToken = getAdminToken();
        String registerUrl = authServerUrl + "/admin/realms/" + realm + "/users";

        Map<String, Object> userPayload = Map.of(
                "username", registrationRequest.getEmail(),
                "email", registrationRequest.getEmail(),
                "firstName", registrationRequest.getFirstname(),
                "lastName", registrationRequest.getLastname(),
                "enabled", true,
                "credentials", List.of(
                        Map.of(
                                "type", "password",
                                "value", registrationRequest.getPassword(),
                                "temporary", false
                        )
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> responseBody = restTemplate.postForEntity(
                    registerUrl,
                    new HttpEntity<>(userPayload, headers),
                    String.class
            );

            if (!responseBody.getStatusCode().is2xxSuccessful()) {
                throw new AppException(ErrorCode.REGISTER_FAIL);
            }

            Users register = userMapper.toUser(registrationRequest);
            register.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            register.setSecret(tfaService.generateSecretKey());

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

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 409) {
                throw new AppException(ErrorCode.EXISTED_USER);
            }
        }
        return "Registration successful! Welcome, " + registrationRequest.getLastname()
                + " " + registrationRequest.getFirstname() + "!";
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        String tokenUrl = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("refresh_token", refreshToken);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, String> responseBody = response.getBody();
            return AuthResponse
                    .builder()
                    .accessToken(responseBody.get("access_token"))
                    .refreshToken(responseBody.get("refresh_token"))
                    .build();
        } else {
            throw new RuntimeException("Failed to refresh token from Keycloak.");
        }
    }
//    private String refreshTokenFromKeycloak(String refreshToken) {
//        String tokenUrl = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";
//        RestTemplate restTemplate = new RestTemplate();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//        body.add("grant_type", "refresh_token");
//        body.add("refresh_token", refreshToken);
//        body.add("client_id", clientId);
//        body.add("client_secret", clientSecret);
//
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
//
//        ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, Map.class);
//
//        if (response.getStatusCode() == HttpStatus.OK) {
//            Map<String, Object> responseBody = response.getBody();
//            return (String) responseBody.get("access_token");
//        } else {
//            throw new RuntimeException("Failed to refresh token from Keycloak.");
//        }
//    }

    @Override
    public String logout(LogoutRequest logoutRequest, HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        if (jwtBlacklistService.isTokenBlacklisted(logoutRequest.getRefreshToken())) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        final String accessToken = authHeader.substring(7);

        try {
            Date accessTokenExpiration = jwtUtil.getKeycloakJwtExpirationTime(accessToken);
            long accessTokenExpirationDuration = accessTokenExpiration.getTime() - System.currentTimeMillis();

            jwtBlacklistService.blacklistedAccessToken(accessToken, accessTokenExpirationDuration);

//            Date refreshTokenExpiration = jwtUtil.getKeycloakJwtExpirationTime(logoutRequest.getRefreshToken());
//            long refreshTokenExpirationDuration = refreshTokenExpiration.getTime() - System.currentTimeMillis();
//
//            jwtBlacklistService.blacklistedRefreshToken(logoutRequest.getRefreshToken(), refreshTokenExpirationDuration);

            return "Log out successful";
        } catch (Exception e) {
            throw new RuntimeException("Logout process failed" + e.getMessage());
        }
    }


//    @Override
//    public AuthResponse verifyAccount(VerificationRequest verificationRequest) {
//        Users user = userRepository.findByEmail(verificationRequest.getEmail())
//                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED_USER));
//
//        if (!tfaService.verifyCode(user.getSecret(), verificationRequest.getOtp())) {
//            throw new AppException(ErrorCode.INVALID_OTP);
//        }
//
//        if (user.isVerified()) {
//            throw new RuntimeException("This account has been verified");
//        }
//
//        try {
//            String accessToken = jwtUtil.generateAccessToken(user);
//            String refreshToken = jwtUtil.generateRefreshToken(user);
//
//            user.setVerified(true);
//            userRepository.save(user);
//            return AuthResponse.builder()
//                    .accessToken(accessToken)
//                    .refreshToken(refreshToken)
//                    .authenticated(true)
//                    .build();
//        } catch (Exception e) {
//            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
//        }
//    }
}
