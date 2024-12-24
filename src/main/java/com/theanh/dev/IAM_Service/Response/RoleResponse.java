package com.theanh.dev.IAM_Service.Response;

import com.theanh.dev.IAM_Service.Dtos.Permission.PermissionDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponse {
    private String name;
    private String description;
    private Set<PermissionDto> permissions;
}