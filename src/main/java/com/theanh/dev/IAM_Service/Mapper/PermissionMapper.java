package com.theanh.dev.IAM_Service.Mapper;

import com.theanh.dev.IAM_Service.Dtos.Permission.PermissionDto;
import com.theanh.dev.IAM_Service.Models.Permissions;
import com.theanh.dev.IAM_Service.Response.Admin.PermissionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    @Mapping(target = "deleted", ignore = true)
    Permissions toPermission(PermissionDto permissionDto);

    PermissionResponse toPermissionResponse(Permissions permissions);
}
