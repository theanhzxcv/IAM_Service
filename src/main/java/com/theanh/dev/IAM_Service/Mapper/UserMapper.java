package com.theanh.dev.IAM_Service.Mapper;

import com.theanh.dev.IAM_Service.Dtos.User.UserDto;
import com.theanh.dev.IAM_Service.Dtos.User.UserUpdateDto;
import com.theanh.dev.IAM_Service.Model.Users;
import com.theanh.dev.IAM_Service.Response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    Users toUser (UserDto userDto);

    UserDto toUserDto(Users user);

    UserResponse toUserRespose(Users user);

    UserUpdateDto toUserUpdateDto(Users user);


//    List<UserDto> toUserDtos(Users users);
}
