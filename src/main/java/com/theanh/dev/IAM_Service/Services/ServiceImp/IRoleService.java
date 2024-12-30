package com.theanh.dev.IAM_Service.Services.ServiceImp;

import com.theanh.dev.IAM_Service.Dtos.Requests.Role.RoleRequest;
import com.theanh.dev.IAM_Service.Dtos.Response.Admin.RoleResponse;

import java.util.List;

public interface IRoleService {

    RoleResponse createRole(RoleRequest roleRequest);

    RoleResponse updateRole(String id, RoleRequest roleRequest);

    List<RoleResponse> allRole();

    String deleteRole(String name);
}
