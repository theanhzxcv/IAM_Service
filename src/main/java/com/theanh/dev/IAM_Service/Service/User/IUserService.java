package com.theanh.dev.IAM_Service.Service.User;

import com.theanh.dev.IAM_Service.Dtos.User.UserDto;
import com.theanh.dev.IAM_Service.Dtos.User.UserUpdateDto;
import com.theanh.dev.IAM_Service.Response.UserResponse;

public interface IUserService {

    UserResponse myProfile();

    UserUpdateDto updateProfile(UserUpdateDto userUpdateDto);
}
