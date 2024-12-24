package com.theanh.dev.IAM_Service.Controllers.Role;

import com.theanh.dev.IAM_Service.Dtos.Permission.PermissionDto;
import com.theanh.dev.IAM_Service.Dtos.Role.RoleDto;
import com.theanh.dev.IAM_Service.Response.ApiResponse;
import com.theanh.dev.IAM_Service.Response.RoleResponse;
import com.theanh.dev.IAM_Service.Services.Role.RoleService;
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
@RequestMapping("/api/role")
public class RoleController {
    RoleService roleService;

    @PostMapping
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(@RequestBody RoleDto roleDto) {
        ApiResponse<RoleResponse> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Role created");
        apiResponse.setData(roleService.createRole(roleDto));

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAllRoles() {
        ApiResponse<List<RoleResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("All permissions");
        apiResponse.setData(roleService.allRole());

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PutMapping
    public ResponseEntity<ApiResponse<RoleResponse>> updateRole(@RequestBody RoleDto roleDto) {
        ApiResponse<RoleResponse> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Update role");
        apiResponse.setData(roleService.updateRole(roleDto));

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<ApiResponse<?>> deletePermission(@PathVariable("name") String roleName) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Delete role");
        apiResponse.setData(roleService.deleteRole(roleName));

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
