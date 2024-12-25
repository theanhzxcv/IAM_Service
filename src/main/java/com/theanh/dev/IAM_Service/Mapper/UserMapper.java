package com.theanh.dev.IAM_Service.Mapper;

import com.theanh.dev.IAM_Service.Dtos.Admin.UserCreateDto;
import com.theanh.dev.IAM_Service.Dtos.Admin.UserUpdateDto;
import com.theanh.dev.IAM_Service.Dtos.Auth.RegistrationDto;
import com.theanh.dev.IAM_Service.Dtos.User.*;
import com.theanh.dev.IAM_Service.Models.Users;
import com.theanh.dev.IAM_Service.Response.User.ShowProfileResponse;
import com.theanh.dev.IAM_Service.Response.Admin.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "secret", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    Users toUser(RegistrationDto registrationDto);

    @Mapping(target = "roles", ignore = true)
    Users toUser(UserCreateDto userCreateDto);

    UserResponse toUserResponse(Users user);

    ShowProfileResponse toShowProfile(Users user);

    UpdateProfileDto toUpdateProfileDto(Users user);

    @Mapping(target = "newPassword", ignore = true)
    @Mapping(target = "confirmationPassword", ignore = true)
    ResetPasswordDto toResetPasswordDto(Users user);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget Users user, UserUpdateDto userUpdateDto);
}
