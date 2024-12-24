package com.theanh.dev.IAM_Service.Services.Role;

import com.theanh.dev.IAM_Service.Dtos.Permission.PermissionDto;
import com.theanh.dev.IAM_Service.Dtos.Role.RoleDto;
import com.theanh.dev.IAM_Service.Response.RoleResponse;

import java.util.List;

public interface IRoleService {

    RoleResponse createRole(RoleDto roleDto);

    RoleResponse updateRole(RoleDto roleDto);

    List<RoleResponse> allRole();

    String deleteRole(String name);
}
