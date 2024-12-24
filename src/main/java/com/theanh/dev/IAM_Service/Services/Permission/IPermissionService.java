package com.theanh.dev.IAM_Service.Services.Permission;

import com.theanh.dev.IAM_Service.Dtos.Permission.PermissionDto;

import java.util.List;

public interface IPermissionService {

    PermissionDto createPermission(PermissionDto permissionDto);

    PermissionDto updatePermission(PermissionDto permissionDto);

    List<PermissionDto> allPermission();

    String deletePermission(String name);
}
