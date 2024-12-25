package com.theanh.dev.IAM_Service.Services.Management;


import com.theanh.dev.IAM_Service.Dtos.Admin.UserCreateDto;
import com.theanh.dev.IAM_Service.Dtos.Admin.UserUpdateDto;
import com.theanh.dev.IAM_Service.Response.Admin.UserResponse;

import java.util.List;

public interface IAdminService {

    UserResponse createUser(UserCreateDto userCreateDto);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(String id);

    UserResponse updateUser(UserUpdateDto userUpdateDto);

    UserResponse banUser(String id);

    String deleteUser(String id);
}
