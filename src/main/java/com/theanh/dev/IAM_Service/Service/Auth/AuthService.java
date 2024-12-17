package com.theanh.dev.IAM_Service.Service.Auth;

import com.theanh.dev.IAM_Service.Dtos.Auth.AuthDto;
import com.theanh.dev.IAM_Service.Dtos.User.UserDto;
import com.theanh.dev.IAM_Service.Mapper.UserMapper;
import com.theanh.dev.IAM_Service.Model.Users;
import com.theanh.dev.IAM_Service.Repository.UserRepository;
import com.theanh.dev.IAM_Service.Response.AuthResponse;
import com.theanh.dev.IAM_Service.Security.JwtUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService implements IAuthService{

    UserRepository userRepository;

    UserMapper userMapper;

    PasswordEncoder passwordEncoder;

    JwtUtil jwtUtil;

    @Override
    public AuthResponse login(AuthDto authDto) {
        var user = userRepository.findByEmail(authDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User existed!"));

        boolean authenticated = passwordEncoder.matches(authDto.getPassword(), user.getPassword());

        if (!authenticated)
            throw new RuntimeException("User unauthenticated");

        var token = jwtUtil.generateToken(authDto.getEmail());

        return AuthResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    @Override
    public UserDto register(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new RuntimeException("User already existed");
        }
        Users register = userMapper.toUser(userDto);
        register.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Users saveUser = userRepository.save(register);

        return userMapper.toUserDto(saveUser);
    }
}
