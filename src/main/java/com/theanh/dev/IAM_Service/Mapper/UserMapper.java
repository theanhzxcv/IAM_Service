package com.theanh.dev.IAM_Service.Mapper;

import com.theanh.dev.IAM_Service.Dtos.User.UserDto;
import com.theanh.dev.IAM_Service.Dtos.User.UserUpdateDto;
import com.theanh.dev.IAM_Service.Model.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    Users toUser (UserDto userDto);

    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    Users toUser(UserUpdateDto userUpdateDto);

    UserDto toUserDto(Users user);


//    List<UserDto> toUserDtos(Users users);
}
