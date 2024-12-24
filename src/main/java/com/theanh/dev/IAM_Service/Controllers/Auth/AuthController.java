package com.theanh.dev.IAM_Service.Controllers.Auth;

import com.theanh.dev.IAM_Service.Dtos.Auth.LoginDto;
import com.theanh.dev.IAM_Service.Dtos.Auth.VerificationDto;
import com.theanh.dev.IAM_Service.Dtos.Auth.RegistrationDto;
import com.theanh.dev.IAM_Service.Response.ApiResponse;
import com.theanh.dev.IAM_Service.Response.AuthResponse;
import com.theanh.dev.IAM_Service.Security.JwtUtil;
import com.theanh.dev.IAM_Service.Services.Auth.AuthService;
import com.theanh.dev.IAM_Service.Services.Blacklist.JwtBlacklistService;
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
@RequestMapping("/api/auth")
public class AuthController {
    JwtUtil jwtUtil;
    AuthService authService;
    JwtBlacklistService jwtBlacklistService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@RequestBody @Valid RegistrationDto registrationDto) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Registered");
        apiResponse.setData(authService.register(registrationDto));

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody @Valid LoginDto loginDto) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Logged in");
        apiResponse.setData(authService.login(loginDto));

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        //return ResponseEntity.ok("A verification email has been sent to your email address. Please check your inbox.");
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(@RequestBody VerificationDto verificationDto,
                                                               HttpServletRequest request) {
        ApiResponse<AuthResponse> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Verified");
        apiResponse.setData(authService.verifyAccount(verificationDto, request));

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        ApiResponse<AuthResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(authService.refreshToken(request, response));
        apiResponse.setMessage("Valid refresh token");

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        long expirationTime = jwtUtil.getExpirationTime(token);
        jwtBlacklistService.addToBlacklist(token);

        return ResponseEntity.status(HttpStatus.OK).body("Logged out successfully");
    }
}
