package com.theanh.dev.IAM_Service.Services.Admin;

import com.theanh.dev.IAM_Service.Dtos.Requests.Permission.PermissionRequest;
import com.theanh.dev.IAM_Service.Mapper.PermissionMapper;
import com.theanh.dev.IAM_Service.Models.Permissions;
import com.theanh.dev.IAM_Service.Repositories.PermissionRepository;
import com.theanh.dev.IAM_Service.Dtos.Response.Admin.PermissionResponse;
import com.theanh.dev.IAM_Service.Services.ServiceImp.IPermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService implements IPermissionService {
    private final PermissionMapper permissionMapper;
    private final PermissionRepository permissionRepository;

    @Override
    public PermissionResponse createPermission(PermissionRequest permissionRequest) {
        var permissions = permissionRepository.save(
                permissionMapper.toPermission(permissionRequest));

        return permissionMapper.toPermissionResponse(permissions);
    }

    @Override
    public PermissionResponse updatePermission(String id, PermissionRequest permissionRequest) {
        var permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("..."));
        if (permissionRequest.getResource() != null) {
            permission.setResource(permissionRequest.getResource());
        }
        if (permissionRequest.getScope() != null) {
            permission.setScope(permissionRequest.getScope());
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
