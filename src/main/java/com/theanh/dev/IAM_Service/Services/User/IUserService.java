package com.theanh.dev.IAM_Service.Services.User;

import com.theanh.dev.IAM_Service.Dtos.User.ChangePasswordDto;
import com.theanh.dev.IAM_Service.Dtos.User.ResetPasswordDto;
import com.theanh.dev.IAM_Service.Dtos.User.UpdateProfileDto;
import com.theanh.dev.IAM_Service.Response.User.ShowProfileResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

public interface IUserService {

    ShowProfileResponse myProfile();

    String updateProfile(UpdateProfileDto updateProfileDto);

    String uploadImage(MultipartFile image);

    String changePassword(ChangePasswordDto changePasswordDto, HttpServletRequest request);

    String forgotPassword(String email);

    String resetPassword(ResetPasswordDto resetPasswordDto, String token, String email);
}
