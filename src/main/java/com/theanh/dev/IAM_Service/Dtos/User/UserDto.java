package com.theanh.dev.IAM_Service.Dtos.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String email;
    private String password;

    private String fullname;
    private String address;
    private Date doB;
}
