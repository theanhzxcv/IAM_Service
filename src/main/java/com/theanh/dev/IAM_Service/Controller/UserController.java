package com.theanh.dev.IAM_Service.Controller;

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

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/users")
public class UserController {

    UserService userService;

    @GetMapping("/myProfile")
    public ResponseEntity<UserResponse> getMyProfile() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.myProfile());
    }

    @PutMapping("/updateProfile")
    public ResponseEntity<UserUpdateDto> updateMyProfile(@RequestBody @Valid UserUpdateDto userUpdateDto) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateProfile(userUpdateDto));
    }
}
