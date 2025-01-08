package com.theanh.dev.IAM_Service.Dtos.Response.Management;

import com.theanh.dev.IAM_Service.Dtos.Response.Admin.RoleResponse;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
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

    private boolean isDeleted;
    private boolean isBanned;

    private String createBy;
    private Timestamp createAt;
    private String lastModifiedBy;
    private Timestamp lastModifiedAt;

    private Set<RoleResponse> roles;
}
