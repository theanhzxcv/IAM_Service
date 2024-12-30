package com.theanh.dev.IAM_Service.Controllers.Role;

import com.theanh.dev.IAM_Service.Dtos.Requests.Role.RoleRequest;
import com.theanh.dev.IAM_Service.Dtos.Response.ApiResponse;
import com.theanh.dev.IAM_Service.Dtos.Response.Admin.RoleResponse;
import com.theanh.dev.IAM_Service.Services.Admin.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/role")
public class RoleController {
    private final RoleService roleService;

//    @PreAuthorize("hasAuthority('role:write')")
    @PostMapping
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(@RequestBody RoleRequest roleRequest) {
        ApiResponse<RoleResponse> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Role created");
        apiResponse.setData(roleService.createRole(roleRequest));

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

//    @PreAuthorize("hasAuthority('role:read')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAllRoles() {
        ApiResponse<List<RoleResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("All permissions");
        apiResponse.setData(roleService.allRole());

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

//    @PreAuthorize("hasAuthority('read:update')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponse>> updateRole(@PathVariable String id, @RequestBody RoleRequest roleRequest) {
        ApiResponse<RoleResponse> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Update role");
        apiResponse.setData(roleService.updateRole(id, roleRequest));

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

//    @PreAuthorize("hasAuthority('read:delete')")
    @DeleteMapping("/{name}")
    public ResponseEntity<ApiResponse<?>> deletePermission(@PathVariable("name") String roleName) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Delete role");
        apiResponse.setData(roleService.deleteRole(roleName));

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
