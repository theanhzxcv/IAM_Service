package com.theanh.dev.IAM_Service.Config;

import com.theanh.dev.IAM_Service.Jwt.JwtFilter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    private final CustomPermissionEvaluator customPermissionEvaluator;

    @Value("${keycloak.enabled}")
    private boolean isKeycloakEnabled;

    String[] PUBLIC_ENDPOINT = {
            "/api/auth/users",
            "/api/auth/tokens",
            "/api/auth/verification",
            "/api/users/password/forgot"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizationManagerRequest ->
                        authorizationManagerRequest
                                .requestMatchers(PUBLIC_ENDPOINT).permitAll()
                                .anyRequest().authenticated());

        if (isKeycloakEnabled) {
            httpSecurity
                    .oauth2ResourceServer(oauth2
                            -> oauth2.jwt(Customizer.withDefaults()));
        } else {
            httpSecurity
                    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        }

//        httpSecurity
//                .addFilterAfter(new BearerTokenAuthenticationFilter(), SecurityContextHolderFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public MethodSecurityExpressionHandler expressionHandler() {
        var expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(customPermissionEvaluator);
        return expressionHandler;
    }
}
