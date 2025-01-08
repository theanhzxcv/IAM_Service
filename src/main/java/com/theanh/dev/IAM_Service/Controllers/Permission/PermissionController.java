package com.theanh.dev.IAM_Service.Controllers.Permission;

import com.theanh.dev.IAM_Service.Dtos.Requests.Permission.PermissionRequest;
import com.theanh.dev.IAM_Service.Dtos.Response.Admin.PermissionResponse;
import com.theanh.dev.IAM_Service.Dtos.Response.ApiResponse;
import com.theanh.dev.IAM_Service.Dtos.Response.ApiResponseBuilder;
import com.theanh.dev.IAM_Service.Services.ServiceImp.Admin.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/permissions")
public class PermissionController {
    private final PermissionService permissionService;

//    @PreAuthorize("hasPermission('permission', 'write')")
    @PostMapping
    public ApiResponse<PermissionResponse> createPermission(@RequestBody PermissionRequest permissionRequest) {
        PermissionResponse newPermission = permissionService.createPermission(permissionRequest);

        return ApiResponseBuilder
                .buildSuccessResponse("Permission created.", newPermission);
    }

//    @PreAuthorize("hasAuthority('permission:read')")
    @GetMapping
    public ApiResponse<List<PermissionResponse>> getAllPermissions() {
        List<PermissionResponse> allPermission = permissionService.allPermission();

        return ApiResponseBuilder
                .buildSuccessResponse("All permission.", allPermission);
    }

//    @PreAuthorize("hasAuthority('permission:update')")
    @PutMapping("/{name}")
    public ApiResponse<PermissionResponse> updatePermission(
            @PathVariable("name") String name,
            @RequestBody PermissionRequest permissionRequest) {
        PermissionResponse updatedPermission = permissionService.updatePermission(name, permissionRequest);

        return ApiResponseBuilder
                .buildSuccessResponse("Permission updated.", updatedPermission);
    }

//    @PreAuthorize("hasAuthority('permission:delete')")
    @DeleteMapping("/{name}")
    public ApiResponse<String> deletePermission(@PathVariable("name") String permissionName) {
        String deletedPermission = permissionService.deletePermission(permissionName);

        return ApiResponseBuilder
                .buildSuccessResponse("Permission deleted.", deletedPermission);
    }
}
