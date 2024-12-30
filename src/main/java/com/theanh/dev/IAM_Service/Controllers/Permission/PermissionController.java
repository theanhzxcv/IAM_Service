package com.theanh.dev.IAM_Service.Controllers.Permission;

import com.theanh.dev.IAM_Service.Dtos.Requests.Permission.PermissionRequest;
import com.theanh.dev.IAM_Service.Dtos.Response.Admin.PermissionResponse;
import com.theanh.dev.IAM_Service.Dtos.Response.ApiResponse;
import com.theanh.dev.IAM_Service.Services.Admin.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/permission")
public class PermissionController {
    private final PermissionService permissionService;

//    @PreAuthorize("hasPermission('permission', 'write')")
    @PostMapping
    public ResponseEntity<ApiResponse<PermissionResponse>> createPermission(@RequestBody PermissionRequest permissionRequest) {
        ApiResponse<PermissionResponse> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Permission created");
        apiResponse.setData(permissionService.createPermission(permissionRequest));

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

//    @PreAuthorize("hasAuthority('permission:read')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getAllPermissions() {
        ApiResponse<List<PermissionResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("All permissions");
        apiResponse.setData(permissionService.allPermission());

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

//    @PreAuthorize("hasAuthority('permission:update')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PermissionResponse>> updatePermission(@PathVariable("id") String id, @RequestBody PermissionRequest permissionRequest) {
        ApiResponse<PermissionResponse> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Update permissions");
        apiResponse.setData(permissionService.updatePermission(id, permissionRequest));

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

//    @PreAuthorize("hasAuthority('permission:delete')")
    @DeleteMapping("/{name}")
    public ResponseEntity<ApiResponse<?>> deletePermission(@PathVariable("name") String permissionName) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Delete permissions");
        apiResponse.setData(permissionService.deletePermission(permissionName));

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
