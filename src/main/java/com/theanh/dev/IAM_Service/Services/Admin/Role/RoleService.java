package com.theanh.dev.IAM_Service.Services.Admin.Role;

import com.theanh.dev.IAM_Service.Dtos.Role.RoleDto;
import com.theanh.dev.IAM_Service.Mapper.RoleMapper;
import com.theanh.dev.IAM_Service.Models.Permissions;
import com.theanh.dev.IAM_Service.Models.Roles;
import com.theanh.dev.IAM_Service.Repositories.PermissionRepository;
import com.theanh.dev.IAM_Service.Repositories.RoleRepository;
import com.theanh.dev.IAM_Service.Response.Admin.RoleResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService implements IRoleService {
    RoleMapper roleMapper;
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;

    @Override
    public RoleResponse createRole(RoleDto roleDto) {
        if (roleRepository.existsByName(roleDto.getName())) {
            throw new RuntimeException("This role existed");
        }
        var role = roleMapper.toRole(roleDto);
        var permissions = permissionRepository.findAllById(roleDto.getPermissions());
        role.setPermissions(new HashSet<>(permissions));
        roleRepository.save(role);

        return roleMapper.toRoleResponse(role);
    }

    @Override
    public RoleResponse updateRole(String id, RoleDto roleDto) {
        var role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("..."));
        if (roleDto.getPermissions() != null) {
            var permissions = permissionRepository.findAllById(roleDto.getPermissions());
            role.setPermissions(new HashSet<>(permissions));

            role.setPermissions(new HashSet<>(permissions));
        }

        Roles saveChange = roleRepository.save(role);

        return roleMapper.toRoleResponse(saveChange);
    }

    @Override
    public List<RoleResponse> allRole() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toRoleResponse).toList();
    }

    @Override
    public String deleteRole(String name) {
        var role = roleRepository.findById(name)
                .orElseThrow(() -> new RuntimeException("Isn't exist that role"));
        role.setDeleted(true);
        roleRepository.save(role);

        return "Role deleted";
    }
}
