package com.theanh.dev.IAM_Service.Services;

import com.theanh.dev.IAM_Service.Dtos.Requests.Admin.UserCreateRequest;
import com.theanh.dev.IAM_Service.Dtos.Requests.Admin.UserSearchRequest;
import com.theanh.dev.IAM_Service.Dtos.Requests.Admin.UserUpdateRequest;
import com.theanh.dev.IAM_Service.Dtos.Response.Management.UserResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IManagementService {

    UserResponse createUser(UserCreateRequest userCreateRequest);

    Page<UserResponse> getAllUsers(int page, int size, String sortBy, String sortDirection);

    UserResponse getUserById(String id);

    Page<UserResponse> searchUserByKeyword(UserSearchRequest request);

    UserResponse updateUser(UserUpdateRequest userUpdateRequest);

    String banUser(String id);

    String unBanUser(String id);

    String deleteUser(String id);
}
