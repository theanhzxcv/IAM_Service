package com.theanh.dev.IAM_Service.Service.Auth;

import com.theanh.dev.IAM_Service.Dtos.Auth.AuthDto;
import com.theanh.dev.IAM_Service.Dtos.User.UserDto;
import com.theanh.dev.IAM_Service.Response.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface IAuthService {
    AuthResponse login(AuthDto authLoginDto);

    UserDto register(UserDto userDto);

    void refreshToken(HttpServletRequest request, HttpServletResponse response);
}
