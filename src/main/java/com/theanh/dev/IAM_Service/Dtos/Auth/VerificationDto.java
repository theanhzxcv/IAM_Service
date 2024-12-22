package com.theanh.dev.IAM_Service.Dtos.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationDto {

    @NotBlank(message = "FIELD_REQUIRED")
    @Email(message = "INVALID_EMAIL")
    private String email;

    @NotNull(message = "FIELD_REQUIRED")
    private int otp;
}
