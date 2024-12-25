package com.theanh.dev.IAM_Service.Services.Admin.Role;

import com.theanh.dev.IAM_Service.Dtos.Role.RoleDto;
import com.theanh.dev.IAM_Service.Response.Admin.RoleResponse;

import java.util.List;

public interface IRoleService {

    RoleResponse createRole(RoleDto roleDto);

    RoleResponse updateRole(String id, RoleDto roleDto);

    List<RoleResponse> allRole();

    String deleteRole(String name);
}
