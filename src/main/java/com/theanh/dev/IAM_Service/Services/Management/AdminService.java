package com.theanh.dev.IAM_Service.Services.Management;

import com.theanh.dev.IAM_Service.Dtos.Admin.UserCreateDto;
import com.theanh.dev.IAM_Service.Dtos.Admin.UserUpdateDto;
import com.theanh.dev.IAM_Service.Exception.AppException;
import com.theanh.dev.IAM_Service.Exception.ErrorCode;
import com.theanh.dev.IAM_Service.Mapper.UserMapper;
import com.theanh.dev.IAM_Service.Models.Users;
import com.theanh.dev.IAM_Service.Repositories.RoleRepository;
import com.theanh.dev.IAM_Service.Repositories.UserRepository;
import com.theanh.dev.IAM_Service.Response.Admin.UserResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
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
    public UserResponse createUser(UserCreateDto userCreateDto) {
        Users user = userMapper.toUser(userCreateDto);
        user.setPassword(passwordEncoder.encode(userCreateDto.getPassword()));

        var roles = roleRepository.findAllById(userCreateDto.getRoles());
        user.setRoles(new HashSet<>(roles));

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.EXISTED_USER);
        }

        return userMapper.toUserResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    @Override
    public UserResponse getUserById(String id) {
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED_USER)));
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
    public UserResponse banUser(String id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED_USER));
        user.setBanned(true);
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @Override
    public String deleteUser(String id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED_USER));
        user.setDeleted(true);
        userRepository.save(user);

        return "User deleted!";
    }
}
