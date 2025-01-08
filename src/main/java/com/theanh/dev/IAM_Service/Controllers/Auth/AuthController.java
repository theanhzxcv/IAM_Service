package com.theanh.dev.IAM_Service.Controllers.Auth;

import com.theanh.dev.IAM_Service.Dtos.Requests.Auth.LoginRequest;
import com.theanh.dev.IAM_Service.Dtos.Requests.Auth.LogoutRequest;
import com.theanh.dev.IAM_Service.Dtos.Requests.Auth.RegistrationRequest;
import com.theanh.dev.IAM_Service.Dtos.Response.ApiResponse;
import com.theanh.dev.IAM_Service.Dtos.Response.ApiResponseBuilder;
import com.theanh.dev.IAM_Service.Dtos.Response.Auth.AuthResponse;
import com.theanh.dev.IAM_Service.Factory.AuthServiceFactory;
import com.theanh.dev.IAM_Service.Jwt.JwtUtil;
import com.theanh.dev.IAM_Service.Services.ServiceImp.Auth.ApplicationAuthService;
import com.theanh.dev.IAM_Service.Services.ServiceImp.Blacklist.JwtBlacklistService;
import com.theanh.dev.IAM_Service.Services.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {
    private final JwtUtil jwtUtil;
    private final AuthServiceFactory authServiceFactory;
    private final ApplicationAuthService applicationAuthService;
    private final JwtBlacklistService jwtBlacklistService;

    @PostMapping("/users")
    public ApiResponse<String> register(@RequestBody @Valid RegistrationRequest registrationRequest) {
        IAuthService authService = authServiceFactory.getAuthService();
        String result = authService.register(registrationRequest);

        return ApiResponseBuilder
                .buildSuccessResponse("Register successful.", result);
    }

    @PostMapping("/tokens")
    public ApiResponse<AuthResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        IAuthService authService = authServiceFactory.getAuthService();
        AuthResponse result = authService.login(loginRequest);

        return ApiResponseBuilder
                .buildSuccessResponse("Login successful. Welcome back.", result);
    }

//    @PostMapping("/verification")
//    public ApiResponse<AuthResponse> verifyOtp(
//            @RequestBody VerificationRequest verificationRequest,
//            HttpServletRequest servletRequest,
//            HttpServletResponse response) {
//
//        IAuthService authService = authServiceFactory.getAuthService();
//        AuthResponse result = authService.verifyAccount(verificationRequest);
//
////        String remoteIp = servletRequest.getHeader("X-Forwarded-For");
////        log.info("ip address: " + remoteIp);
////        log.info("Http response status: " + response.getStatus());
//        return ApiResponseBuilder
//                .buildSuccessResponse("Verify successful.", result);
//    }

    @PostMapping("/tokens/refresh")
    public ApiResponse<AuthResponse> refreshToken(@RequestParam String refreshToken)
            throws IOException {
        IAuthService authService = authServiceFactory.getAuthService();
        AuthResponse result = authService.refreshToken(refreshToken);

        return ApiResponseBuilder
                .buildSuccessResponse("Token refreshed.", result);

    }

    @PostMapping("logout")
    public ApiResponse<String> logout(@RequestBody LogoutRequest logoutRequest, HttpServletRequest request) {
        IAuthService authService = authServiceFactory.getAuthService();
        String result = authService.logout(logoutRequest, request);
        return ApiResponseBuilder
                .buildSuccessResponse("Logged out", result);
    }
}
