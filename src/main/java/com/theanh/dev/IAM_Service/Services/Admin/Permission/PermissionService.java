package com.theanh.dev.IAM_Service.Services.Admin.Permission;

import com.theanh.dev.IAM_Service.Dtos.Permission.PermissionDto;
import com.theanh.dev.IAM_Service.Mapper.PermissionMapper;
import com.theanh.dev.IAM_Service.Models.Permissions;
import com.theanh.dev.IAM_Service.Repositories.PermissionRepository;
import com.theanh.dev.IAM_Service.Response.Admin.PermissionResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService implements IPermissionService {
    PermissionMapper permissionMapper;
    PermissionRepository permissionRepository;

    @Override
    public PermissionResponse createPermission(PermissionDto permissionDto) {
        var permissions = permissionRepository.save(
                permissionMapper.toPermission(permissionDto));

        return permissionMapper.toPermissionResponse(permissions);
    }

    @Override
    public PermissionResponse updatePermission(String id, PermissionDto permissionDto) {
        var permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("..."));
        if (permissionDto.getResource() != null) {
            permission.setResource(permissionDto.getResource());
        }
        if (permissionDto.getScope() != null) {
            permission.setScope(permissionDto.getScope());
        }

        Permissions saveChange = permissionRepository.save(permission);

        return permissionMapper.toPermissionResponse(saveChange);
    }

    @Override
    public List<PermissionResponse> allPermission() {
        return permissionRepository.findAll()
                .stream()
                .map(permissionMapper::toPermissionResponse).toList();
    }

    @Override
    public String deletePermission(String name) {
        var permissions = permissionRepository.findById(name)
                .orElseThrow(() -> new RuntimeException("Isn't exist that permission"));
        permissions.setDeleted(true);
        permissionRepository.save(permissions);

        return "Permission deleted!";
    }
}
