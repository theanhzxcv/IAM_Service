package com.theanh.dev.IAM_Service.Response.Admin;

import com.theanh.dev.IAM_Service.Dtos.Permission.PermissionDto;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponse {
    private String id;
    private String name;
    private Set<PermissionDto> permissions;
}