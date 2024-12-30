package com.theanh.dev.IAM_Service.Mapper;

import com.theanh.dev.IAM_Service.Dtos.Requests.Permission.PermissionRequest;
import com.theanh.dev.IAM_Service.Models.Permissions;
import com.theanh.dev.IAM_Service.Dtos.Response.Admin.PermissionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    @Mapping(target = "deleted", ignore = true)
    Permissions toPermission(PermissionRequest permissionRequest);

    PermissionResponse toPermissionResponse(Permissions permissions);
}
