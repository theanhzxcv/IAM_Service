package com.theanh.dev.IAM_Service.Response.Admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowProfileResponse {
    private String email;
    private String firstname;
    private String lastname;
    private String address;
    private String imageUrl;
    private int phone;
    private Date doB;
}
