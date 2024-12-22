package com.theanh.dev.IAM_Service.Services.User;

import com.theanh.dev.IAM_Service.Dtos.User.ChangePasswordDto;
import com.theanh.dev.IAM_Service.Dtos.User.ResetPasswordDto;
import com.theanh.dev.IAM_Service.Dtos.User.UserUpdateDto;
import com.theanh.dev.IAM_Service.Response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

public interface IUserService {

    UserResponse myProfile();

    UserUpdateDto updateProfile(UserUpdateDto userUpdateDto);

    String uploadImage(MultipartFile image);

    String changePassword(ChangePasswordDto changePasswordDto);

    String forgotPassword(String email);

    String resetPassword(ResetPasswordDto resetPasswordDto, String token, String email);
}
