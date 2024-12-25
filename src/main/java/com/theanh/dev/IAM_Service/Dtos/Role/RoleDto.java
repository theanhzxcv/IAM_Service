package com.theanh.dev.IAM_Service.Dtos.Role;

import com.theanh.dev.IAM_Service.Dtos.Permission.PermissionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {
    private String name;
    private Set<String> permissions;
}
