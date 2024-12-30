package com.theanh.dev.IAM_Service.Controllers.Auth;

import com.theanh.dev.IAM_Service.Dtos.Requests.Auth.LoginRequest;
import com.theanh.dev.IAM_Service.Dtos.Requests.Auth.VerificationRequest;
import com.theanh.dev.IAM_Service.Dtos.Requests.Auth.RegistrationRequest;
import com.theanh.dev.IAM_Service.Dtos.Response.ApiResponse;
import com.theanh.dev.IAM_Service.Dtos.Response.Auth.AuthResponse;
import com.theanh.dev.IAM_Service.Jwt.JwtUtil;
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
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final JwtBlacklistService jwtBlacklistService;

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<?>> register(@RequestBody @Valid RegistrationRequest registrationRequest,
                                                   HttpServletRequest request) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Registered");
        apiResponse.setData(authService.register(registrationRequest, request));

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping("/tokens")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody @Valid LoginRequest loginRequest) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Logged in");
        apiResponse.setData(authService.login(loginRequest));

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        //return ResponseEntity.ok("A verification email has been sent to your email address. Please check your inbox.");
    }

    @PostMapping("/verification")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(@RequestBody VerificationRequest verificationRequest) {
        ApiResponse<AuthResponse> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Verified");
        apiResponse.setData(authService.verifyAccount(verificationRequest));

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping("/tokens/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(HttpServletRequest request,
                                                                  HttpServletResponse response)
            throws IOException {
        ApiResponse<AuthResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(authService.refreshToken(request, response));
        apiResponse.setMessage("Valid refresh token");

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

//    @PostMapping("/logout")
//    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authorizationHeader) {
//        String token = authorizationHeader.substring(7);
//        long expirationTime = jwtUtil.getExpirationTime(token);
//        jwtBlacklistService.addToBlacklist(token);
//
//        return ResponseEntity.status(HttpStatus.OK).body("Logged out successfully");
//    }
}
