package com.theanh.dev.IAM_Service.Jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Arrays;

@Component
public class JwtConfig {
    @Value("${keycloak.client.secret}")
    private String clientSecret;
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Bean
    public JwtDecoder decoder() {
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }
}
