package com.theanh.dev.IAM_Service.Controllers.Admin;

import com.theanh.dev.IAM_Service.Dtos.Requests.Admin.UserCreateRequest;
import com.theanh.dev.IAM_Service.Dtos.Requests.Admin.UserUpdateRequest;
import com.theanh.dev.IAM_Service.Dtos.Response.ApiResponse;
import com.theanh.dev.IAM_Service.Dtos.Response.Admin.UserResponse;
import com.theanh.dev.IAM_Service.Services.Management.AdminService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/management")
public class ManagementController {
    private final AdminService adminService;

//    @PreAuthorize("hasAuthority('user:create')")
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody UserCreateRequest userCreateRequest) {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Created user");
        apiResponse.setData(adminService.createUser(userCreateRequest));

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

//    @PreAuthorize("hasAuthority('user:read')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUser() {
        ApiResponse<List<UserResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("All users");
        apiResponse.setData(adminService.getAllUsers());

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

//    @PreAuthorize("hasAuthority('user:read')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable String id) {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Users with id: " + id);
        apiResponse.setData(adminService.getUserById(id));

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

//    @PreAuthorize("hasAuthority('user:update')")
    @PutMapping
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("All users");
        apiResponse.setData(adminService.updateUser(userUpdateRequest));

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

//    @PreAuthorize("hasAuthority('user:ban')")
    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> banUser(@PathVariable String id) {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Ban users");
        apiResponse.setData(adminService.banUser(id));

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

//    @PreAuthorize("hasAuthority('user:delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteUser(@PathVariable String id) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Delete users");
        apiResponse.setData(adminService.deleteUser(id));

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

}
