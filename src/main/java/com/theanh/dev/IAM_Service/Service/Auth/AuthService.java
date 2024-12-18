package com.theanh.dev.IAM_Service.Service.Auth;

import com.theanh.dev.IAM_Service.Dtos.Auth.AuthDto;
import com.theanh.dev.IAM_Service.Dtos.User.UserDto;
import com.theanh.dev.IAM_Service.Exception.AppException;
import com.theanh.dev.IAM_Service.Exception.ErrorCode;
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
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean authenticated = passwordEncoder.matches(authDto.getPassword(), user.getPassword());

        if (!authenticated)
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        String token = null;

        try {
            token = jwtUtil.generateToken(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return AuthResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    @Override
    public UserDto register(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        if (userDto.getEmail().isEmpty() || userDto.getPassword().isEmpty()){
            throw new AppException(ErrorCode.INCOMPLETE_DETAIL);
        }

        Users register = userMapper.toUser(userDto);
        register.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Users saveUser = userRepository.save(register);

        return userMapper.toUserDto(saveUser);
    }
}
