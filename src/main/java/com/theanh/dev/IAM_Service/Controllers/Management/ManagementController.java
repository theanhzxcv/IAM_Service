package com.theanh.dev.IAM_Service.Controllers.Management;

import com.theanh.dev.IAM_Service.Dtos.Requests.Admin.UserCreateRequest;
import com.theanh.dev.IAM_Service.Dtos.Requests.Admin.UserSearchRequest;
import com.theanh.dev.IAM_Service.Dtos.Requests.Admin.UserUpdateRequest;
import com.theanh.dev.IAM_Service.Dtos.Response.ApiResponse;
import com.theanh.dev.IAM_Service.Dtos.Response.ApiResponseBuilder;
import com.theanh.dev.IAM_Service.Dtos.Response.Management.UserResponse;
import com.theanh.dev.IAM_Service.Services.ServiceImp.Management.ManagementService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class ManagementController {
    private final ManagementService managementService;

//    @PreAuthorize("hasAuthority('user:create')")
    @PostMapping
    public ApiResponse<UserResponse> createUser(@RequestBody UserCreateRequest userCreateRequest) {
        UserResponse newUser = managementService.createUser(userCreateRequest);

        return ApiResponseBuilder
                .createdSuccessResponse("New user created.", newUser);
    }

//    @PreAuthorize("hasAuthority('user:read')")
    @GetMapping
    public ApiResponse<Page<UserResponse>> getAllUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        Page<UserResponse> allUser = managementService.getAllUsers(page, size, sortBy, sortDirection);

        return ApiResponseBuilder
                .buildSuccessResponse("All user.", allUser);
    }

//    @PreAuthorize("hasAuthority('user:read')")
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUserById(@PathVariable String id) {
        UserResponse foundUser = managementService.getUserById(id);

        return ApiResponseBuilder
                .buildSuccessResponse("User with id: " + id, foundUser);
    }

    @GetMapping("/search")
    public ApiResponse<Page<UserResponse>> searchUsers(@ParameterObject UserSearchRequest userSearchRequest) {

        // Create the UserSearchRequest object based on request parameters

        // Pass the request object to the service to fetch the paginated results
        return ApiResponseBuilder.buildSuccessResponse("Users with keyword: ",
                managementService.searchUserByKeyword(userSearchRequest));
    }

//    @PreAuthorize("hasAuthority('user:update')")
    @PutMapping
    public ApiResponse<UserResponse> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        UserResponse updatedUser = managementService.updateUser(userUpdateRequest);

        return ApiResponseBuilder
                .buildSuccessResponse("User updated successful.", updatedUser);
    }

//    @PreAuthorize("hasAuthority('user:ban')")
    @PutMapping("/{id}/ban")
    public ApiResponse<String> banUser(@PathVariable String id) {
        String bannedUser = managementService.banUser(id);

        return ApiResponseBuilder
                .buildSuccessResponse("Ban user successful.", bannedUser);
    }

    @PutMapping("/{id}/unban")
    public ApiResponse<String> unBanUser(@PathVariable String id) {
        String unBannedUser = managementService.unBanUser(id);

        return ApiResponseBuilder
                .buildSuccessResponse("Unban user successful.", unBannedUser);
    }

//    @PreAuthorize("hasAuthority('user:delete')")
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteUser(@PathVariable String id) {
        String deletedUser = managementService.deleteUser(id);

        return ApiResponseBuilder
                .buildSuccessResponse("Delete user successful", deletedUser);
    }

}
