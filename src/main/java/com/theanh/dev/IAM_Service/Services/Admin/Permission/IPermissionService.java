package com.theanh.dev.IAM_Service.Services.Admin.Permission;

import com.theanh.dev.IAM_Service.Dtos.Permission.PermissionDto;
import com.theanh.dev.IAM_Service.Response.Admin.PermissionResponse;

import java.util.List;

public interface IPermissionService {

    PermissionResponse createPermission(PermissionDto permissionDto);

    PermissionResponse updatePermission(String id, PermissionDto permissionDto);

    List<PermissionResponse> allPermission();

    String deletePermission(String name);
}
