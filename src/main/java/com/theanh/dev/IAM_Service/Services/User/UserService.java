package com.theanh.dev.IAM_Service.Services.User;

import com.theanh.dev.IAM_Service.Dtos.User.ChangePasswordDto;
import com.theanh.dev.IAM_Service.Dtos.User.ResetPasswordDto;
import com.theanh.dev.IAM_Service.Dtos.User.UpdateProfileDto;
import com.theanh.dev.IAM_Service.Exception.AppException;
import com.theanh.dev.IAM_Service.Exception.ErrorCode;
import com.theanh.dev.IAM_Service.Mapper.UserMapper;
import com.theanh.dev.IAM_Service.Models.UserActivity;
import com.theanh.dev.IAM_Service.Models.Users;
import com.theanh.dev.IAM_Service.Repositories.UserActivityRepository;
import com.theanh.dev.IAM_Service.Repositories.UserRepository;
import com.theanh.dev.IAM_Service.Response.Admin.ShowProfileResponse;
import com.theanh.dev.IAM_Service.Security.JwtUtil;
import com.theanh.dev.IAM_Service.Services.Email.EmailService;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService implements IUserService {
    JwtUtil jwtUtil;
    UserMapper userMapper;
    EmailService emailService;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    UserActivityRepository userActivityRepository;

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
    public ShowProfileResponse myProfile() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username {}", authentication.getName());

        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

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
    public String updateProfile(UpdateProfileDto updateProfileDto) {
        String email = getCurrentUserEmail();

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED_USER));

        if (updateProfileDto.getFirstname() != null) {
            user.setFirstname(updateProfileDto.getFirstname());
        }
        if (updateProfileDto.getLastname() != null) {
            user.setLastname(updateProfileDto.getLastname());
        }
        if (updateProfileDto.getAddress() != null) {
            user.setAddress(updateProfileDto.getAddress());
        }
        if (updateProfileDto.getPhone() != 0) {
            user.setPhone(updateProfileDto.getPhone());
        }
        if (updateProfileDto.getDoB() != null) {
            user.setDoB(updateProfileDto.getDoB());
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
    public String changePassword(ChangePasswordDto changePasswordDto, HttpServletRequest request) {
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
            emailService.sendPasswordChangeEmail(user.getEmail(), changePasswordDto.getNewPassword());
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
    public String resetPassword(ResetPasswordDto resetPasswordDto, String token, String email) {
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
            return "Your password has been reset" +
            "\nYou can now log in with your new password";
        } catch (Exception e) {
            throw new AppException(ErrorCode.SESSION_EXPIRED);
        }

    }
}
