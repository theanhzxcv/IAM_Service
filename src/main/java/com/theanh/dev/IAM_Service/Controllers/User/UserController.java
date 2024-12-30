package com.theanh.dev.IAM_Service.Controllers.User;

import com.theanh.dev.IAM_Service.Dtos.Requests.User.ChangePasswordRequest;
import com.theanh.dev.IAM_Service.Dtos.Requests.User.ResetPasswordRequest;
import com.theanh.dev.IAM_Service.Dtos.Requests.User.UpdateProfileRequest;
import com.theanh.dev.IAM_Service.Dtos.Response.User.ProfileResponse;
import com.theanh.dev.IAM_Service.Dtos.Response.ApiResponse;
import com.theanh.dev.IAM_Service.Services.User.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class UserController {
    private final UserService userService;

//    @PreAuthorize("hasPermission('null', 'profile.delete')")
    @GetMapping
    public ApiResponse<ProfileResponse> getMyProfile() {
        ProfileResponse profileResponse = userService.myProfile();

        return ApiResponse
                .<ProfileResponse>builder()
                .code(200)
                .status("success")
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .message(profileResponse.getLastname() + " "
                        + profileResponse.getFirstname() + "'s profile.")
                .data(profileResponse)
                .build();
    }

    @PatchMapping
    public ApiResponse<?> updateMyProfile(@RequestBody @Valid UpdateProfileRequest updateProfileRequest) {
        String result = userService.updateProfile(updateProfileRequest);

        return ApiResponse
                .<String>builder()
                .code(200)
                .status("success")
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .message("Profile updated successful.")
                .data(result)
                .build();
    }

//    @PostMapping("/avatar")
//    public ApiResponse<?> uploadAvatar(@RequestBody MultipartFile image) {
//        String result = userService.uploadImage(image);
//
//        return ApiResponse
//                .<String>builder()
//                .code(200)
//                .status("success")
//                .timestamp(new Timestamp(System.currentTimeMillis()))
//                .message("Avatar uploaded successful.")
//                .data(result)
//                .build();
//    }

    @PatchMapping("/password")
    public ApiResponse<?> changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest, HttpServletRequest request) {
        String result = userService.changePassword(changePasswordRequest, request);

        return ApiResponse
                .<String>builder()
                .code(200)
                .status("success")
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .message("Password changed successful.")
                .data(result)
                .build();
    }

    @PostMapping("/password/forgot")
    public ApiResponse<?> forgotPassword(@RequestParam String email) {
        String result = userService.forgotPassword(email);

        return ApiResponse
                .<String>builder()
                .code(200)
                .status("success")
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .message("Reset password email sent.")
                .data(result)
                .build();
    }

//    @GetMapping("/password/reset")
//    public ApiResponse<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest,
//                                                             @RequestParam String token,
//                                                             @RequestParam String email
//    ) {
//        String result = userService.resetPassword(resetPasswordRequest, token, email);
//
//        return ApiResponse
//                .<String>builder()
//                .code(200)
//                .status("success")
//                .timestamp(new Timestamp(System.currentTimeMillis()))
//                .message("Password reseted successful.")
//                .data(result)
//                .build();
//    }
}
