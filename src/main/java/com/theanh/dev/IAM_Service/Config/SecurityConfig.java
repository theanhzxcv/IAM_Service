package com.theanh.dev.IAM_Service.Config;

import com.theanh.dev.IAM_Service.Security.JwtFilter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
//@AllArgsConstructor
public class SecurityConfig {

    JwtFilter jwtFilter;
    CustomPermissionEvaluator customPermissionEvaluator;

    String[] WHITELIST_ENDPOINT = {
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/verify",
            "/api/users/forgot-password",
//            "/api/permission/**",
//            "/api/role/**",
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizationManagerRequest ->
                        authorizationManagerRequest
                                .requestMatchers(WHITELIST_ENDPOINT).permitAll()
                                .requestMatchers("/api/management/**").hasRole("MANAGEMENT")
//                                .requestMatchers("/api/role/**").hasRole("ADMIN")
//                                .requestMatchers("/api/permission/**").hasRole("ADMIN")
                                .requestMatchers("/api/users/**").hasAnyRole("USER", "ADMIN", "MANAGEMENT")

                                .anyRequest().authenticated())

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public PermissionEvaluator permissionEvaluator() {
        return customPermissionEvaluator;
    }
}
