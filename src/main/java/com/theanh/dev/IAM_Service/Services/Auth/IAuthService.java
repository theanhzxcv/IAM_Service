package com.theanh.dev.IAM_Service.Services.Auth;

import com.theanh.dev.IAM_Service.Dtos.Auth.LoginDto;
import com.theanh.dev.IAM_Service.Dtos.Auth.RegistrationDto;
import com.theanh.dev.IAM_Service.Dtos.Auth.VerificationDto;
import com.theanh.dev.IAM_Service.Response.Auth.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface IAuthService {

    String login(LoginDto authLoginDto);

    String register(RegistrationDto registrationDto, HttpServletRequest request);

    AuthResponse refreshToken(HttpServletRequest request, HttpServletResponse response);

    AuthResponse verifyAccount(VerificationDto verificationDto);
}
