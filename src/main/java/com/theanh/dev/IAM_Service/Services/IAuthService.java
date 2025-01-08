package com.theanh.dev.IAM_Service.Services;

import com.theanh.dev.IAM_Service.Dtos.Requests.Auth.LoginRequest;
import com.theanh.dev.IAM_Service.Dtos.Requests.Auth.LogoutRequest;
import com.theanh.dev.IAM_Service.Dtos.Requests.Auth.RegistrationRequest;
import com.theanh.dev.IAM_Service.Dtos.Response.Auth.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface IAuthService {

    AuthResponse login(LoginRequest loginRequest);

//    AuthResponse verifyAccount(VerificationRequest verificationRequest);

    String register(RegistrationRequest registrationRequest);

    AuthResponse refreshToken(String refreshToken);

    String logout(LogoutRequest logoutRequest, HttpServletRequest request);


}
