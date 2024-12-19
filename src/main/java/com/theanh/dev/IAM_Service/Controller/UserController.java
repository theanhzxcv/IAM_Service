package com.theanh.dev.IAM_Service.Controller;

import com.theanh.dev.IAM_Service.Dtos.User.ChangePasswordDto;
import com.theanh.dev.IAM_Service.Dtos.User.ResetPasswordDto;
import com.theanh.dev.IAM_Service.Dtos.User.UserDto;
import com.theanh.dev.IAM_Service.Dtos.User.UserUpdateDto;
import com.theanh.dev.IAM_Service.Response.UserResponse;
import com.theanh.dev.IAM_Service.Service.User.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/users")
public class UserController {

    UserService userService;

    @GetMapping("/my-profile")
    public ResponseEntity<UserResponse> getMyProfile() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.myProfile());
    }

    @PatchMapping("/update-profile")
    public ResponseEntity<UserUpdateDto> updateMyProfile(@RequestBody @Valid UserUpdateDto userUpdateDto) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateProfile(userUpdateDto));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody @Valid ChangePasswordDto changePasswordDto) {
        userService.changePassword(changePasswordDto);
        return ResponseEntity.status(HttpStatus.OK).body("Password changes successfully!");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> changePassword(@RequestParam String email) {
        userService.forgotPassword(email);
        return ResponseEntity.status(HttpStatus.OK).body("Reset password sent!");
    }

    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto, @RequestParam String token, @RequestParam String email) {
        userService.resetPassword(resetPasswordDto, token, email);
        return ResponseEntity.status(HttpStatus.OK).body("Your password has been reset." +
                "\nYou can now log in with your new password: " + resetPasswordDto.getNewPassword());
    }
}
