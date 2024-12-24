package com.theanh.dev.IAM_Service.Dtos.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class UpdateProfileDto {

    @NotBlank(message = "FIELD_REQUIRED")
    private String firstname;

    @NotBlank(message = "FIELD_REQUIRED")
    private String lastname;

    @NotBlank(message = "FIELD_REQUIRED")
    private String address;

    @NotNull(message = "FIELD_REQUIRED")
    private int phone;

    @NotNull(message = "FIELD_REQUIRED")
    private Date doB;
}
