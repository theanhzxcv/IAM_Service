package com.theanh.dev.IAM_Service.Service.User;

import com.theanh.dev.IAM_Service.Model.Users;
import com.theanh.dev.IAM_Service.Repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService implements UserDetailsService {

    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            var userObj = user.get();
            return User.builder()
                    .username(userObj.getUsername())
                    .password(userObj.getPassword())
                    .build();
        }else{
            throw new UsernameNotFoundException(username);
        }
    }
}
