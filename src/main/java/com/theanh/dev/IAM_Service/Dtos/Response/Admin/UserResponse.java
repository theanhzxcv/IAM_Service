package com.theanh.dev.IAM_Service.Dtos.Response.Admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String email;
    private String password;
    private String firstname;
    private String lastname;
    private String address;
    private String imageUrl;
    private int phone;
    private Date doB;
    private Set<RoleResponse> roles;
}
