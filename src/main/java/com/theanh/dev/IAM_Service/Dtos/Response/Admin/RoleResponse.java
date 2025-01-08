package com.theanh.dev.IAM_Service.Dtos.Response.Admin;

import com.theanh.dev.IAM_Service.Dtos.Requests.Permission.PermissionRequest;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponse {
    private String name;
    private Set<PermissionRequest> permissions;
}