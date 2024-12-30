package com.theanh.dev.IAM_Service.Dtos.Response.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
    private String email;
    private String firstname;
    private String lastname;
    private String address;
    private String imageUrl;
    private int phone;
    private Date doB;
}
