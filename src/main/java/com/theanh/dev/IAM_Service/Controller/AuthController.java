package com.theanh.dev.IAM_Service.Controller;

import com.theanh.dev.IAM_Service.Dtos.Auth.AuthDto;
import com.theanh.dev.IAM_Service.Dtos.User.UserDto;
import com.theanh.dev.IAM_Service.Model.InvalidToken;
import com.theanh.dev.IAM_Service.Response.ApiResponse;
import com.theanh.dev.IAM_Service.Response.AuthResponse;
import com.theanh.dev.IAM_Service.Security.JwtUtil;
import com.theanh.dev.IAM_Service.Service.Auth.AuthService;
import com.theanh.dev.IAM_Service.Service.Blacklist.JwtBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/auth")
public class AuthController {

    JwtUtil jwtUtil;

    AuthService authService;

    JwtBlacklistService jwtBlacklistService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserDto userDto) {
        return ResponseEntity.ok(authService.register(userDto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthDto authDto) {
        return ResponseEntity.ok(authService.login(authDto));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        authService.refreshToken(request, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        long expirationTime = jwtUtil.getExpirationTime(token);
        jwtBlacklistService.addToBlacklist(token, expirationTime);
        return ResponseEntity.ok("Logged out successfully");
    }
}
