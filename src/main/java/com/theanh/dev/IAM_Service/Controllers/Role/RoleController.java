package com.theanh.dev.IAM_Service.Controllers.Role;

import com.theanh.dev.IAM_Service.Dtos.Requests.Role.RoleRequest;
import com.theanh.dev.IAM_Service.Dtos.Response.ApiResponse;
import com.theanh.dev.IAM_Service.Dtos.Response.Admin.RoleResponse;
import com.theanh.dev.IAM_Service.Dtos.Response.ApiResponseBuilder;
import com.theanh.dev.IAM_Service.Services.ServiceImp.Admin.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roles")
public class RoleController {
    private final RoleService roleService;

//    @PreAuthorize("hasAuthority('role:write')")
    @PostMapping
    public ApiResponse<RoleResponse> createRole(@RequestBody RoleRequest roleRequest) {
        RoleResponse newRole = roleService.createRole(roleRequest);

        return ApiResponseBuilder
                .buildSuccessResponse("Role created.", newRole);
    }

//    @PreAuthorize("hasAuthority('role:read')")
    @GetMapping
    public ApiResponse<List<RoleResponse>> getAllRoles() {
        List<RoleResponse> allRole = roleService.allRole();

        return ApiResponseBuilder
                .buildSuccessResponse("All role.", allRole);
    }

//    @PreAuthorize("hasAuthority('read:update')")
    @PutMapping("/{name}")
    public ApiResponse<RoleResponse> updateRole(@PathVariable String name,
                                                @RequestBody RoleRequest roleRequest) {
        RoleResponse updatedRole = roleService.updateRole(name, roleRequest);

        return ApiResponseBuilder
                .buildSuccessResponse("Role updated..", updatedRole);
    }

//    @PreAuthorize("hasAuthority('read:delete')")
    @DeleteMapping("/{name}")
    public ApiResponse<String> deleteRole(@PathVariable("name") String roleName) {
        String deletedRole = roleService.deleteRole(roleName);

        return ApiResponseBuilder
                .buildSuccessResponse("Role deleted.", deletedRole);
    }
}
