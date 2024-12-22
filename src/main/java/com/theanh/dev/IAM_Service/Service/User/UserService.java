package com.theanh.dev.IAM_Service.Service.User;

import com.theanh.dev.IAM_Service.Dtos.User.ChangePasswordDto;
import com.theanh.dev.IAM_Service.Dtos.User.ResetPasswordDto;
import com.theanh.dev.IAM_Service.Dtos.User.UserDto;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService implements IUserService {

    JwtUtil jwtUtil;
    UserMapper userMapper;
    EmailService emailService;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    public String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    public UserResponse myProfile() {
        String email = getCurrentUserEmail();

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED_USER));

        try {
            return userMapper.toUserResponse(user);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    @Override
    public UserUpdateDto updateProfile(UserUpdateDto userUpdateDto) {
        String email = getCurrentUserEmail();

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED_USER));

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
        try {
            Users updateProfile = userRepository.save(user);
            emailService.sendProfileUpdateEmail(updateProfile.getEmail());
            return userMapper.toUserUpdateDto(updateProfile);
        } catch (Exception e) {
            throw new AppException(ErrorCode.SESSION_EXPIRED);
        }

    }

    @Override
    public void uploadImage(MultipartFile image, Principal connectedUser) {

    }

    @Override
    public String changePassword(ChangePasswordDto changePasswordDto) {
        String email = getCurrentUserEmail();

        Users user =  userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED_USER));

        if (!changePasswordDto.getEmail().equals(user.getEmail())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        if (!passwordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmationPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        if (changePasswordDto.getNewPassword().isEmpty()) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        try {
            user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
            userRepository.save(user);
            emailService.sendPasswordChangeEmail(user.getEmail(), changePasswordDto.getNewPassword());
        } catch (Exception e) {
            throw new AppException(ErrorCode.SESSION_EXPIRED);
        }


        return "Password changed";
//        return userMapper.toChangePasswordDto(user);
    }

    @Override
    public String forgotPassword(String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED_USER));
        try {
            String resetToken = jwtUtil.generateAccessToken(user);
            emailService.sendResetPasswordEmail(email, resetToken);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        return "Reset instructions have been sent to your email address. " +
                "Check your inbox or spam folder.";
    }

    @Override
    public void resetPassword(ResetPasswordDto resetPasswordDto, String token, String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED_USER));

        if (!jwtUtil.isTokenValid(token, user)) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
        if (!resetPasswordDto.getNewPassword().equals(resetPasswordDto.getConfirmationPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        try {
            user.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
            userRepository.save(user);
        } catch (Exception e) {
            throw new AppException(ErrorCode.SESSION_EXPIRED);
        }

//        return userMapper.toResetPasswordDto(user);
    }
}
