package com.theanh.dev.IAM_Service.Mapper;

import com.theanh.dev.IAM_Service.Dtos.Requests.Admin.UserCreateRequest;
import com.theanh.dev.IAM_Service.Dtos.Requests.Admin.UserUpdateRequest;
import com.theanh.dev.IAM_Service.Dtos.Requests.Auth.RegistrationRequest;
import com.theanh.dev.IAM_Service.Dtos.Requests.User.ResetPasswordRequest;
import com.theanh.dev.IAM_Service.Dtos.Requests.User.UpdateProfileRequest;
import com.theanh.dev.IAM_Service.Dtos.Response.Management.UserResponse;
import com.theanh.dev.IAM_Service.Models.Users;
import com.theanh.dev.IAM_Service.Dtos.Response.User.ProfileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "secret", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    Users toUser(RegistrationRequest registrationRequest);

    @Mapping(target = "roles", ignore = true)
    Users toUser(UserCreateRequest userCreateRequest);

    UserResponse toUserResponse(Users user);

    List<UserResponse> toUserResponse(List<Users> user);

    ProfileResponse toShowProfile(Users user);

    UpdateProfileRequest toUpdateProfileDto(Users user);

    @Mapping(target = "newPassword", ignore = true)
    @Mapping(target = "confirmationPassword", ignore = true)
    ResetPasswordRequest toResetPasswordDto(Users user);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget Users user, UserUpdateRequest userUpdateRequest);
}
