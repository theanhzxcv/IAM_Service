package com.theanh.dev.IAM_Service.Service.User;

import com.theanh.dev.IAM_Service.Dtos.User.ChangePasswordDto;
import com.theanh.dev.IAM_Service.Dtos.User.ResetPasswordDto;
import com.theanh.dev.IAM_Service.Dtos.User.UserUpdateDto;
import com.theanh.dev.IAM_Service.Exception.AppException;
import com.theanh.dev.IAM_Service.Exception.ErrorCode;
import com.theanh.dev.IAM_Service.Mapper.UserMapper;
import com.theanh.dev.IAM_Service.Model.Users;
import com.theanh.dev.IAM_Service.Repository.UserRepository;
import com.theanh.dev.IAM_Service.Response.UserResponse;
import com.theanh.dev.IAM_Service.Security.JwtUtil;
import com.theanh.dev.IAM_Service.Service.Email.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService implements IUserService {

    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    UserMapper userMapper;

    EmailService emailService;

    JwtUtil jwtUtil;

    @Override
    public UserResponse myProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }

    @Override
    public UserUpdateDto updateProfile(UserUpdateDto userUpdateDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (userUpdateDto.getFirstname() != null) {
            user.setFirstname(userUpdateDto.getFirstname());
        }
        if (userUpdateDto.getLastname() != null) {
            user.setLastname(userUpdateDto.getLastname());
        }
        if (userUpdateDto.getAddress() != null) {
            user.setAddress(userUpdateDto.getAddress());
        }
        if (userUpdateDto.getPhone() != 0) {
            user.setPhone(userUpdateDto.getPhone());
        }
        if (userUpdateDto.getDoB() != null) {
            user.setDoB(userUpdateDto.getDoB());
        }
        Users updateProfile = userRepository.save(user);

        emailService.sendProfileUpdateEmail(updateProfile.getEmail());

        return userMapper.toUserUpdateDto(updateProfile);
    }

    @Override
    public void changePassword(ChangePasswordDto changePasswordDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Users user =  userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!changePasswordDto.getEmail().equals(user.getEmail())) {
            throw new AppException(ErrorCode.INCORRECT_EMAIL);
        }

        if (!passwordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        }

        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmationPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        if (!changePasswordDto.getNewPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        }

        emailService.sendPasswordChangeEmail(user.getEmail(), changePasswordDto.getNewPassword());

        userRepository.save(user);
    }

    @Override
    public void forgotPassword(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        String resetToken;
        try {
            resetToken = jwtUtil.generateAccessToken(user);
            emailService.sendResetPasswordEmail(email, resetToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void resetPassword(ResetPasswordDto resetPasswordDto, String token, String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        jwtUtil.isTokenValid(token, user);

        if (!resetPasswordDto.getNewPassword().equals(resetPasswordDto.getConfirmationPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        user.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));

        userRepository.save(user);
    }
}
