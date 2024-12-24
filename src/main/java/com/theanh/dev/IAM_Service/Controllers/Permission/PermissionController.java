package com.theanh.dev.IAM_Service.Controllers.Permission;

import com.theanh.dev.IAM_Service.Dtos.Permission.PermissionDto;
import com.theanh.dev.IAM_Service.Response.ApiResponse;
import com.theanh.dev.IAM_Service.Services.Permission.PermissionService;
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
@RequestMapping("/api/permission")
public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    public ResponseEntity<ApiResponse<PermissionDto>> createPermission(@RequestBody PermissionDto permissionDto) {
        ApiResponse<PermissionDto> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Permission created");
        apiResponse.setData(permissionService.createPermission(permissionDto));

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PermissionDto>>> getAllPermissions() {
        ApiResponse<List<PermissionDto>> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("All permissions");
        apiResponse.setData(permissionService.allPermission());

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PutMapping
    public ResponseEntity<ApiResponse<PermissionDto>> updatePermission(@RequestBody PermissionDto permissionDto) {
        ApiResponse<PermissionDto> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Update permissions");
        apiResponse.setData(permissionService.updatePermission(permissionDto));

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<ApiResponse<?>> deletePermission(@PathVariable("name") String permissionName) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Delete permissions");
        apiResponse.setData(permissionService.deletePermission(permissionName));

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
