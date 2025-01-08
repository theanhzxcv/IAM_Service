package com.theanh.dev.IAM_Service.Dtos.Requests.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

//    @NotBlank(message = "FIELD_REQUIRED")
//    @Email(message = "INVALID_CREDENTIALS")
    private String email;

//    @NotBlank(message = "FIELD_REQUIRED")
//    @Pattern(regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=_]).{8,}$", message = "INSECURE_PASSWORD")
    private String password;
}
