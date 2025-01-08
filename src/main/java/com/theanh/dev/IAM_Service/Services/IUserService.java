package com.theanh.dev.IAM_Service.Services;

import com.theanh.dev.IAM_Service.Dtos.Requests.User.ChangePasswordRequest;
import com.theanh.dev.IAM_Service.Dtos.Requests.User.ResetPasswordRequest;
import com.theanh.dev.IAM_Service.Dtos.Requests.User.UpdateProfileRequest;
import com.theanh.dev.IAM_Service.Dtos.Response.User.ProfileResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

public interface IUserService {

    ProfileResponse myProfile();

    String updateProfile(UpdateProfileRequest updateProfileRequest);

    String uploadImage(MultipartFile image);

    String changePassword(ChangePasswordRequest changePasswordRequest, HttpServletRequest request);

    String forgotPassword(String email);

    String resetPassword(ResetPasswordRequest resetPasswordRequest, String token, String email);
}
