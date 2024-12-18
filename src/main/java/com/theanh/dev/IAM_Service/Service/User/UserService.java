package com.theanh.dev.IAM_Service.Service.User;

import com.theanh.dev.IAM_Service.Dtos.User.UserUpdateDto;
import com.theanh.dev.IAM_Service.Exception.AppException;
import com.theanh.dev.IAM_Service.Exception.ErrorCode;
import com.theanh.dev.IAM_Service.Mapper.UserMapper;
import com.theanh.dev.IAM_Service.Model.Users;
import com.theanh.dev.IAM_Service.Repository.UserRepository;
import com.theanh.dev.IAM_Service.Response.UserResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService implements IUserService {

    UserRepository userRepository;

    UserMapper userMapper;

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Optional<Users> user = userRepository.findByEmail(username);
//        if (user.isPresent()) {
//            var userObj = user.get();
//            return User.builder()
//                    .username(userObj.getUsername())
//                    .password(userObj.getPassword())
//                    .build();
//        }else{
//            throw new UsernameNotFoundException(username);
//        }
//    }

    @Override
    public UserResponse myProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserRespose(user);
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

        return userMapper.toUserUpdateDto(updateProfile);
    }
}
