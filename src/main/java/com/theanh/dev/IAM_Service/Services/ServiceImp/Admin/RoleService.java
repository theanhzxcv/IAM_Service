package com.theanh.dev.IAM_Service.Services.ServiceImp.Admin;

import com.theanh.dev.IAM_Service.Dtos.Requests.Role.RoleRequest;
import com.theanh.dev.IAM_Service.Mapper.RoleMapper;
import com.theanh.dev.IAM_Service.Models.Roles;
import com.theanh.dev.IAM_Service.Repositories.PermissionRepository;
import com.theanh.dev.IAM_Service.Repositories.RoleRepository;
import com.theanh.dev.IAM_Service.Dtos.Response.Admin.RoleResponse;
import com.theanh.dev.IAM_Service.Services.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {
    private final RoleMapper roleMapper;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public RoleResponse createRole(RoleRequest roleRequest) {
        if (roleRepository.findById(roleRequest.getName()).isPresent()) {
            throw new RuntimeException("This role existed");
        }
        var role = roleMapper.toRole(roleRequest);
        var permissions = permissionRepository.findAllById(roleRequest.getPermissions());
        role.setPermissions(new HashSet<>(permissions));
        roleRepository.save(role);

        return roleMapper.toRoleResponse(role);
    }

    @Override
    public RoleResponse updateRole(String name, RoleRequest roleRequest) {
        var role = roleRepository.findById(name)
                .orElseThrow(() -> new RuntimeException("..."));
        if (roleRequest.getPermissions() != null) {
            var permissions = permissionRepository.findAllById(roleRequest.getPermissions());
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
