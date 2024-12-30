package com.theanh.dev.IAM_Service.Services.User;

import com.theanh.dev.IAM_Service.Dtos.Requests.User.ChangePasswordRequest;
import com.theanh.dev.IAM_Service.Dtos.Requests.User.ResetPasswordRequest;
import com.theanh.dev.IAM_Service.Dtos.Requests.User.UpdateProfileRequest;
import com.theanh.dev.IAM_Service.Dtos.Response.User.ProfileResponse;
import com.theanh.dev.IAM_Service.Exception.AppException;
import com.theanh.dev.IAM_Service.Exception.ErrorCode;
import com.theanh.dev.IAM_Service.Mapper.UserMapper;
import com.theanh.dev.IAM_Service.Models.UserActivity;
import com.theanh.dev.IAM_Service.Models.Users;
import com.theanh.dev.IAM_Service.Repositories.UserActivityRepository;
import com.theanh.dev.IAM_Service.Repositories.UserRepository;
import com.theanh.dev.IAM_Service.Jwt.JwtUtil;
import com.theanh.dev.IAM_Service.Services.Email.EmailService;
import com.theanh.dev.IAM_Service.Services.ServiceImp.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserActivityRepository userActivityRepository;

    public String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    @Override
    public ProfileResponse myProfile() {
        String email = getCurrentUserEmail();
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED_USER));

        try {
            return userMapper.toShowProfile(user);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    @Override
    public String updateProfile(UpdateProfileRequest updateProfileRequest) {
        String email = getCurrentUserEmail();

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED_USER));

        if (updateProfileRequest.getFirstname() != null) {
            user.setFirstname(updateProfileRequest.getFirstname());
        }
        if (updateProfileRequest.getLastname() != null) {
            user.setLastname(updateProfileRequest.getLastname());
        }
        if (updateProfileRequest.getAddress() != null) {
            user.setAddress(updateProfileRequest.getAddress());
        }
        if (updateProfileRequest.getPhone() != 0) {
            user.setPhone(updateProfileRequest.getPhone());
        }
        if (updateProfileRequest.getDoB() != null) {
            user.setDoB(updateProfileRequest.getDoB());
        }

        try {
            Users updateProfile = userRepository.save(user);
            emailService.sendProfileUpdateEmail(updateProfile.getEmail());
            return "Profile updated successfully";
        } catch (Exception e) {
            throw new AppException(ErrorCode.SESSION_EXPIRED);
        }
    }

    @Override
    public String uploadImage(MultipartFile image) {
        String email = getCurrentUserEmail();
        Users user =  userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED_USER));

        if (image.isEmpty()) {
            return "No file selected.";
        }

        try {
            String UPLOAD_DIR = "src/main/resources/image/";
            String filename = UUID.randomUUID() + "-" + image.getOriginalFilename();
            Path filepath = Paths.get(UPLOAD_DIR + filename);
            Files.createDirectories(filepath.getParent());
            Files.write(filepath, image.getBytes());

            String avatarUrl = filepath.toString();
            user.setImageUrl(avatarUrl);
            userRepository.save(user);
            return "Avatar uploaded successfully: " + avatarUrl;
        } catch (IOException e) {
            return "Failed to upload avatar.";
        }
    }

    @Override
    public String changePassword(ChangePasswordRequest changePasswordRequest, HttpServletRequest request) {
        String email = getCurrentUserEmail();

        Users user =  userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED_USER));

        if (!changePasswordRequest.getEmail().equals(user.getEmail())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmationPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        if (changePasswordRequest.getNewPassword().isEmpty()) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        try {
            user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
            String clientIp = getClientIp(request);
            if ("0:0:0:0:0:0:0:1".equals(clientIp)) {
                clientIp = "127.0.0.1";
            }
            UserActivity userActivity = UserActivity
                    .builder()
                    .ip(clientIp)
                    .email(user.getEmail())
                    .activity("Change password")
                    .timestamp(new Date())
                    .build();
            userActivityRepository.save(userActivity);
            userRepository.save(user);
            emailService.sendPasswordChangeEmail(user.getEmail(), changePasswordRequest.getNewPassword());
        } catch (Exception e) {
            throw new AppException(ErrorCode.SESSION_EXPIRED);
        }
        return "Password changed successfully.";
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
    public String resetPassword(ResetPasswordRequest resetPasswordRequest, String token, String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED_USER));

        if (!jwtUtil.isTokenValid(token, user)) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
        if (!resetPasswordRequest.getNewPassword().equals(resetPasswordRequest.getConfirmationPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        try {
            user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
            userRepository.save(user);
            return "Your password has been reset" +
            "\nYou can now log in with your new password";
        } catch (Exception e) {
            throw new AppException(ErrorCode.SESSION_EXPIRED);
        }

    }
}
