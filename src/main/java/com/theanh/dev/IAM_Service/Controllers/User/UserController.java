package com.theanh.dev.IAM_Service.Controllers.User;

import com.theanh.dev.IAM_Service.Dtos.User.ChangePasswordDto;
import com.theanh.dev.IAM_Service.Dtos.User.ResetPasswordDto;
import com.theanh.dev.IAM_Service.Dtos.User.UpdateProfileDto;
import com.theanh.dev.IAM_Service.Response.User.ShowProfileResponse;
import com.theanh.dev.IAM_Service.Response.ApiResponse;
import com.theanh.dev.IAM_Service.Services.User.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/users")
public class UserController {
    UserService userService;

    @GetMapping("/my-profile")
    public ResponseEntity<ApiResponse<ShowProfileResponse>> getMyProfile() {
        ApiResponse<ShowProfileResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(userService.myProfile());

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PatchMapping("/update-profile")
    public ResponseEntity<ApiResponse<?>> updateMyProfile(@RequestBody @Valid UpdateProfileDto updateProfileDto) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Updated profile");
        apiResponse.setData(userService.updateProfile(updateProfileDto));

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping("/upload-avatar")
    public ResponseEntity<ApiResponse<?>> uploadAvatar(@RequestBody MultipartFile image) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Uploaded image");
        apiResponse.setData(userService.uploadImage(image));

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PatchMapping("/change-password")
    public ResponseEntity<ApiResponse<?>> changePassword(@RequestBody @Valid ChangePasswordDto changePasswordDto, HttpServletRequest request) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Password changed");
        apiResponse.setData(userService.changePassword(changePasswordDto, request));

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<?>> changePassword(@RequestParam String email) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Forgot password");
        apiResponse.setData(userService.forgotPassword(email));

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto,
                                                             @RequestParam String token,
                                                             @RequestParam String email
    ) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Password reset");
        apiResponse.setData(userService.resetPassword(resetPasswordDto, token, email));

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
