package com.theanh.dev.IAM_Service.Service.User;

import com.theanh.dev.IAM_Service.Dtos.User.ChangePasswordDto;
import com.theanh.dev.IAM_Service.Dtos.User.ResetPasswordDto;
import com.theanh.dev.IAM_Service.Dtos.User.UserUpdateDto;
import com.theanh.dev.IAM_Service.Response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

public interface IUserService {

    UserResponse myProfile();

    UserUpdateDto updateProfile(UserUpdateDto userUpdateDto);

    void uploadImage(MultipartFile image, Principal connectedUser);

    String changePassword(ChangePasswordDto changePasswordDto);

    String forgotPassword(String email);

    void resetPassword(ResetPasswordDto resetPasswordDto, String token, String email);
}
