package com.theanh.dev.IAM_Service.Mapper;

import com.theanh.dev.IAM_Service.Dtos.User.ChangePasswordDto;
import com.theanh.dev.IAM_Service.Dtos.User.ResetPasswordDto;
import com.theanh.dev.IAM_Service.Dtos.User.UserDto;
import com.theanh.dev.IAM_Service.Dtos.User.UserUpdateDto;
import com.theanh.dev.IAM_Service.Models.Users;
import com.theanh.dev.IAM_Service.Response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    Users toUser(UserDto userDto);

    UserDto toUserDto(Users user);

    UserResponse toUserResponse(Users user);

    UserUpdateDto toUserUpdateDto(Users user);

    @Mapping(target = "newPassword", ignore = true)
    @Mapping(target = "confirmationPassword", ignore = true)
    ResetPasswordDto toResetPasswordDto(Users user);

    ChangePasswordDto toChangePasswordDto(Users user);


//    List<UserDto> toUserDtos(Users users);
}
