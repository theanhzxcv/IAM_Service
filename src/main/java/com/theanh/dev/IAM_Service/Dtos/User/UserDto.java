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

    @Size(min = 3, max = 20, message = "INVALID_NAME")
    private String firstname;

    @Size(min = 3, max = 20, message = "INVALID_NAME")
    private String lastname;

    @Email(message = "INVALID_EMAIL")
    private String email;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=_]).{8,}$", message = "INVALID_PASSWORD")
    private String password;

    private String address;

    @NotNull
    private int phone;

    @NotNull
    @Past(message = "INVALID_DOB")
    private Date doB;
}
