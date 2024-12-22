package com.theanh.dev.IAM_Service.Controllers;

import com.theanh.dev.IAM_Service.Dtos.User.ChangePasswordDto;
import com.theanh.dev.IAM_Service.Dtos.User.ResetPasswordDto;
import com.theanh.dev.IAM_Service.Dtos.User.UserUpdateDto;
import com.theanh.dev.IAM_Service.Response.ApiResponse;
import com.theanh.dev.IAM_Service.Response.UserResponse;
import com.theanh.dev.IAM_Service.Services.User.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/users")
public class UserController {
    UserService userService;

    @GetMapping("/my-profile")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile() {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("success");
        apiResponse.setDetails(userService.myProfile());
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PatchMapping("/update-profile")
    public ResponseEntity<ApiResponse<UserUpdateDto>> updateMyProfile(@RequestBody @Valid UserUpdateDto userUpdateDto) {
        ApiResponse<UserUpdateDto> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("success");
        apiResponse.setDetails(userService.updateProfile(userUpdateDto));
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping("/upload-avatar")
    public ResponseEntity<ApiResponse<?>> uploadAvatar(@RequestBody MultipartFile image) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("success");
        apiResponse.setMessage("Uploaded image");
        apiResponse.setDetails(userService.uploadImage(image));
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PatchMapping("/change-password")
    public ResponseEntity<ApiResponse<?>> changePassword(@RequestBody @Valid ChangePasswordDto changePasswordDto) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("success");
        apiResponse.setMessage("Password changed");
        apiResponse.setDetails(userService.changePassword(changePasswordDto));

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<?>> changePassword(@RequestParam String email) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("success");
        apiResponse.setMessage("Forgot password");
        apiResponse.setDetails(userService.forgotPassword(email));

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PutMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto,
                                                             @RequestParam String token,
                                                             @RequestParam String email
    ) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("Success");
        apiResponse.setMessage("Password reset");
        apiResponse.setDetails(userService.resetPassword(resetPasswordDto, token, email));
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
