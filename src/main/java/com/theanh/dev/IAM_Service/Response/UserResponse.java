package com.theanh.dev.IAM_Service.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private String email;
    private String username;
    private String fullname;
    private String address;
    private int phone;
    private Date doB;
}
