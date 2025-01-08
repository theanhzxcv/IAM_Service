package com.theanh.dev.IAM_Service.Dtos.Requests.Auth;

import lombok.Data;

@Data
public class LogoutRequest {
    String refreshToken;
}
