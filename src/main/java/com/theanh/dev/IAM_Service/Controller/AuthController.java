package com.theanh.dev.IAM_Service.Controller;

import com.theanh.dev.IAM_Service.Dtos.Auth.AuthDto;
import com.theanh.dev.IAM_Service.Dtos.User.UserDto;
import com.theanh.dev.IAM_Service.Response.ApiResponse;
import com.theanh.dev.IAM_Service.Response.AuthResponse;
import com.theanh.dev.IAM_Service.Service.Auth.AuthService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/auth")
public class AuthController {

    AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody @Valid UserDto userDto) {
        return ResponseEntity.ok(authService.register(userDto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthDto authDto) {
        return ResponseEntity.ok(authService.login(authDto));
    }
}
