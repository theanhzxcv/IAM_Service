package com.theanh.dev.IAM_Service.Controller;

import com.theanh.dev.IAM_Service.Dtos.Auth.AuthDto;
import com.theanh.dev.IAM_Service.Dtos.User.UserDto;
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
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<String> register(@RequestBody @Valid UserDto userDto) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.register(userDto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthDto authDto) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(authDto));
        //return ResponseEntity.ok("A verification email has been sent to your email address. Please check your inbox.");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(authService.refreshToken(request, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        long expirationTime = jwtUtil.getExpirationTime(token);
        jwtBlacklistService.addToBlacklist(token);
        return ResponseEntity.status(HttpStatus.OK).body("Logged out successfully");
    }
}
