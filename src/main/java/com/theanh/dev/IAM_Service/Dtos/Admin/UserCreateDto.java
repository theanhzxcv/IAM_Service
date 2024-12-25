package com.theanh.dev.IAM_Service.Dtos.Admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class UserCreateDto {

    @NotBlank(message = "FIELD_REQUIRED")
    private String firstname;

    @NotBlank(message = "FIELD_REQUIRED")
    private String lastname;

    @NotBlank(message = "FIELD_REQUIRED")
    @Email(message = "INVALID_EMAIL")
    private String email;

    @NotBlank(message = "FIELD_REQUIRED")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=_]).{8,}$", message = "INSECURE_PASSWORD")
    private String password;

    @NotBlank(message = "FIELD_REQUIRED")
    private String address;

    private String imageUrl;

    @NotNull(message = "FIELD_REQUIRED")
    private int phone;

    @NotNull(message = "FIELD_REQUIRED")
    private Date doB;

    private Set<String> roles;

    private boolean isDeleted;

    private boolean isBanned;
}
