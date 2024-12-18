package com.theanh.dev.IAM_Service.Dtos.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.util.Date;

@Data
public class UserUpdateDto {

    @NotBlank
    private String firstname;

    @NotBlank
    private String lastname;

    @NotBlank
    private String address;

    @NotNull
    private int phone;

    @NotNull
    @Past(message = "INVALID_DOB")
    private Date doB;
}
