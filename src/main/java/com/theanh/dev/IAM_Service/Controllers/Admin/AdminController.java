package com.theanh.dev.IAM_Service.Controllers.Admin;

import com.theanh.dev.IAM_Service.Dtos.Admin.UserUpdateDto;
import com.theanh.dev.IAM_Service.Response.ApiResponse;
import com.theanh.dev.IAM_Service.Response.UserResponse;
import com.theanh.dev.IAM_Service.Services.Admin.AdminService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/admin")
public class AdminController {
    AdminService adminService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUser() {
        ApiResponse<List<UserResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("All users");
        apiResponse.setData(adminService.getALlUsers());
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PutMapping
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@RequestBody UserUpdateDto userUpdateDto) {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("All users");
        apiResponse.setData(adminService.updateUser(userUpdateDto));
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
