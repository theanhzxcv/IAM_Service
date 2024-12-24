package com.theanh.dev.IAM_Service.Services.Admin;

import com.theanh.dev.IAM_Service.Dtos.Admin.UserUpdateDto;
import com.theanh.dev.IAM_Service.Exception.AppException;
import com.theanh.dev.IAM_Service.Exception.ErrorCode;
import com.theanh.dev.IAM_Service.Mapper.UserMapper;
import com.theanh.dev.IAM_Service.Models.Users;
import com.theanh.dev.IAM_Service.Repositories.RoleRepository;
import com.theanh.dev.IAM_Service.Repositories.UserRepository;
import com.theanh.dev.IAM_Service.Response.UserResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminService implements IAdminService{
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    UserRepository userRepository;
    UserMapper userMapper;

    @Override
    public List<UserResponse> getALlUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    @Override
    public UserResponse updateUser(UserUpdateDto userUpdateDto) {
        Users user = userRepository.findByEmail(userUpdateDto.getEmail()).orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED_USER));

        userMapper.updateUser(user, userUpdateDto);
        user.setPassword(passwordEncoder.encode(userUpdateDto.getPassword()));

        var roles = roleRepository.findAllById(userUpdateDto.getRoles());
        user.setRoles(new HashSet<>(roles));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse getUserById(String id) {
        return null;
    }
}
