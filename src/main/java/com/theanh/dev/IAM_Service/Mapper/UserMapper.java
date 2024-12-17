package com.theanh.dev.IAM_Service.Mapper;

import com.theanh.dev.IAM_Service.Dtos.User.UserDto;
import com.theanh.dev.IAM_Service.Model.Users;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    Users toUser (UserDto userDto);

    UserDto toUserDto(Users user);

//    List<UserDto> toUserDtos(Users users);
}
