package com.theanh.dev.IAM_Service.Config;

import com.theanh.dev.IAM_Service.Exception.AppException;
import com.theanh.dev.IAM_Service.Exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.util.annotation.NonNull;

import java.util.Optional;

@Component
public class MyAuditorAware implements AuditorAware<String> {
    @Value("${keycloak.enabled}")
    private boolean isKeycloakEnabled;

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        if (isKeycloakEnabled) {
            if (authentication instanceof JwtAuthenticationToken) {
                Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
                return Optional.of(jwt.getClaimAsString("email"));
            }
        } else {
            return Optional.of(authentication.getName());
        }
        return Optional.of("System");
    }
}
