package com.theanh.dev.IAM_Service.Controllers.User;

import com.theanh.dev.IAM_Service.Dtos.Requests.User.ChangePasswordRequest;
import com.theanh.dev.IAM_Service.Dtos.Requests.User.UpdateProfileRequest;
import com.theanh.dev.IAM_Service.Dtos.Response.ApiResponseBuilder;
import com.theanh.dev.IAM_Service.Dtos.Response.User.ProfileResponse;
import com.theanh.dev.IAM_Service.Dtos.Response.ApiResponse;
import com.theanh.dev.IAM_Service.Services.ServiceImp.User.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class UserController {
    private final UserService userService;

    @GetMapping
    public ApiResponse<ProfileResponse> getMyProfile() {
        ProfileResponse profileResponse = userService.myProfile();

        return ApiResponseBuilder
                .buildSuccessResponse(profileResponse.getLastname()
                + profileResponse.getFirstname()
                + "'s profile.", profileResponse);
    }

    @PatchMapping
    public ApiResponse<String> updateProfile(
            @RequestBody @Valid UpdateProfileRequest updateProfileRequest) {
        String updatedProfile = userService.updateProfile(updateProfileRequest);

        return ApiResponseBuilder
                .buildSuccessResponse("Profile updated successful.", updatedProfile);
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
    public ApiResponse<String> changePassword(
            @RequestBody @Valid ChangePasswordRequest changePasswordRequest,
                                         HttpServletRequest request) {
        String changedPassword = userService.changePassword(changePasswordRequest, request);

        return ApiResponseBuilder
                .buildSuccessResponse("Password changed successful.",
                        changedPassword);
    }

    @PostMapping("/password/forgot")
    public ApiResponse<String> forgotPassword(@RequestParam String email) {
        String forgotPassword = userService.forgotPassword(email);

        return ApiResponseBuilder
                .buildSuccessResponse("Reset password email sent.",
                        forgotPassword);
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
