package com.theanh.dev.IAM_Service.Service.Auth;

import com.theanh.dev.IAM_Service.Dtos.Auth.AuthDto;
import com.theanh.dev.IAM_Service.Dtos.User.UserDto;
import com.theanh.dev.IAM_Service.Response.AuthResponse;

public interface IAuthService {
    AuthResponse login(AuthDto authLoginDto);

    UserDto register(UserDto userDto);
}
