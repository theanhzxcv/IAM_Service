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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService implements IAuthService{

    UserRepository userRepository;

    UserMapper userMapper;

    PasswordEncoder passwordEncoder;

    JwtUtil jwtUtil;

    EmailService emailService;

    @Override
    public AuthResponse login(AuthDto authDto) {
        var user = userRepository.findByEmail(authDto.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean authenticated = passwordEncoder.matches(authDto.getPassword(), user.getPassword());

        if (!authenticated)
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        String accessToken = null;

        String refreshToken = null;

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

//        return userMapper.toUserDto(saveUser);
        return "Registered successfully!";
    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        refreshToken = authHeader.substring(7);
        userEmail = jwtUtil.extractUsername(refreshToken);

        if (userEmail != null) {
            var user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            if (jwtUtil.isTokenValid(refreshToken, user)) {
                String accessToken = null;
                try {
                    accessToken = jwtUtil.generateAccessToken(user);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                var authResponse = AuthResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                try {
                    new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
