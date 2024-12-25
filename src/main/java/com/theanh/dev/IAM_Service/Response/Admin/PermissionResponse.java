package com.theanh.dev.IAM_Service.Response.Admin;

import com.theanh.dev.IAM_Service.Dtos.Permission.PermissionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionResponse {
    private String name;
    private String resource;
    private String scope;
}