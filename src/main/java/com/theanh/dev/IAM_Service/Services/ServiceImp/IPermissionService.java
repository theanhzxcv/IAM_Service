package com.theanh.dev.IAM_Service.Services.ServiceImp;

import com.theanh.dev.IAM_Service.Dtos.Requests.Permission.PermissionRequest;
import com.theanh.dev.IAM_Service.Dtos.Response.Admin.PermissionResponse;

import java.util.List;

public interface IPermissionService {

    PermissionResponse createPermission(PermissionRequest permissionRequest);

    PermissionResponse updatePermission(String id, PermissionRequest permissionRequest);

    List<PermissionResponse> allPermission();

    String deletePermission(String name);
}
