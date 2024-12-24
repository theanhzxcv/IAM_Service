package com.theanh.dev.IAM_Service.Mapper;

import com.theanh.dev.IAM_Service.Dtos.Permission.PermissionDto;
import com.theanh.dev.IAM_Service.Models.Permissions;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    Permissions toPermission(PermissionDto permissionDto);

    PermissionDto toPermissionDtos(Permissions permissions);
}
