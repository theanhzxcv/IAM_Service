package com.theanh.dev.IAM_Service.Services.ServiceImp;

import com.theanh.dev.IAM_Service.Dtos.Requests.Auth.LoginRequest;
import com.theanh.dev.IAM_Service.Dtos.Requests.Auth.RegistrationRequest;
import com.theanh.dev.IAM_Service.Dtos.Requests.Auth.VerificationRequest;
import com.theanh.dev.IAM_Service.Dtos.Response.Auth.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface IAuthService {

    String login(LoginRequest authLoginRequest);

    String register(RegistrationRequest registrationRequest, HttpServletRequest request);

    AuthResponse refreshToken(HttpServletRequest request, HttpServletResponse response);

    AuthResponse verifyAccount(VerificationRequest verificationRequest);
}
