package com.theanh.dev.IAM_Service.Services.ServiceImp;


import com.theanh.dev.IAM_Service.Dtos.Requests.Admin.UserCreateRequest;
import com.theanh.dev.IAM_Service.Dtos.Requests.Admin.UserUpdateRequest;
import com.theanh.dev.IAM_Service.Dtos.Response.Admin.UserResponse;

import java.util.List;

public interface IAdminService {

    UserResponse createUser(UserCreateRequest userCreateRequest);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(String id);

    UserResponse updateUser(UserUpdateRequest userUpdateRequest);

    UserResponse banUser(String id);

    String deleteUser(String id);
}
