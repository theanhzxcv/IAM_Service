package com.theanh.dev.IAM_Service.Services.Admin;


import com.theanh.dev.IAM_Service.Dtos.Admin.UserUpdateDto;
import com.theanh.dev.IAM_Service.Response.UserResponse;

import java.util.List;

public interface IAdminService {

    List<UserResponse> getALlUsers();

    UserResponse updateUser(UserUpdateDto userUpdateDto);

    UserResponse getUserById(String id);

}
