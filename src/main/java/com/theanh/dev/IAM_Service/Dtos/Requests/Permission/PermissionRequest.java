package com.theanh.dev.IAM_Service.Dtos.Requests.Permission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionRequest {
    private String name;
    private String resource;
    private String scope;
}
