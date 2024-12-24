package com.theanh.dev.IAM_Service.Mapper;

import com.theanh.dev.IAM_Service.Dtos.Role.RoleDto;
import com.theanh.dev.IAM_Service.Models.Roles;
import com.theanh.dev.IAM_Service.Response.RoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "permissions", ignore = true)
    Roles toRole(RoleDto roleDto);

    RoleResponse toRoleResponse(Roles roles);
}
