package com.theanh.dev.IAM_Service.Dtos.User;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {

    @NotBlank
    private String fullname;

    @NotBlank
    private String address;

    @NotNull
    private int phone;

    @NotNull
    @Past(message = "INVALID_DOB")
    private Date doB;
}
