package com.theanh.dev.IAM_Service.Dtos.User;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

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

    @NotNull(message = "FIELD_REQUIRED")
    private int phone;

    @NotNull(message = "FIELD_REQUIRED")
    private Date doB;

}
