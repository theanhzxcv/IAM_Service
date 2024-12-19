package com.theanh.dev.IAM_Service.Service.User;

import com.theanh.dev.IAM_Service.Dtos.User.ChangePasswordDto;
import com.theanh.dev.IAM_Service.Dtos.User.ResetPasswordDto;
import com.theanh.dev.IAM_Service.Dtos.User.UserUpdateDto;
import com.theanh.dev.IAM_Service.Response.UserResponse;

import java.security.Principal;

public interface IUserService {

    UserResponse myProfile();

    UserUpdateDto updateProfile(UserUpdateDto userUpdateDto);

    void changePassword(ChangePasswordDto changePasswordDto);

    void forgotPassword(String email);

    void resetPassword(ResetPasswordDto resetPasswordDto, String token, String email);
}
