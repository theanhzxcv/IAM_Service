package com.theanh.dev.IAM_Service.Dtos.Requests.Auth;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Date;

@Data
public class RegistrationRequest {

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
}