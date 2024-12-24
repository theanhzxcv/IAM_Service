package com.theanh.dev.IAM_Service.Services.Permission;

import com.theanh.dev.IAM_Service.Dtos.Permission.PermissionDto;
import com.theanh.dev.IAM_Service.Mapper.PermissionMapper;
import com.theanh.dev.IAM_Service.Models.Permissions;
import com.theanh.dev.IAM_Service.Repositories.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService implements IPermissionService{
    PermissionMapper permissionMapper;
    PermissionRepository permissionRepository;

    @Override
    public PermissionDto createPermission(PermissionDto permissionDto) {
        var permissions = permissionRepository.save(
                permissionMapper.toPermission(permissionDto));

        return permissionMapper.toPermissionDtos(permissions);
    }

    @Override
    public PermissionDto updatePermission(PermissionDto permissionDto) {
        var permission = permissionRepository.findById(permissionDto.getName())
                .orElseThrow(() -> new RuntimeException("..."));
        if (permissionDto.getName() != null) {
            permission.setName(permissionDto.getName());
        }
        if (permissionDto.getDescription() != null) {
            permission.setDescription(permissionDto.getDescription());
        }

        Permissions saveChange = permissionRepository.save(permission);

        return permissionMapper.toPermissionDtos(saveChange);
    }

    @Override
    public List<PermissionDto> allPermission() {
        return permissionRepository.findAll()
                .stream()
                .map(permissionMapper::toPermissionDtos).toList();
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
