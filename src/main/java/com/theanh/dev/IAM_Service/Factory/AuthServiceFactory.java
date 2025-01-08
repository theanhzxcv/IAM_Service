package com.theanh.dev.IAM_Service.Factory;

import com.theanh.dev.IAM_Service.Services.ServiceImp.Auth.ApplicationAuthService;
import com.theanh.dev.IAM_Service.Services.ServiceImp.Auth.KeycloakAuthService;
import com.theanh.dev.IAM_Service.Services.IAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthServiceFactory {
    @Value("${keycloak.enabled}")
    private boolean isKeycloakEnabled;

    private final KeycloakAuthService keycloakAuthService;
    private final ApplicationAuthService applicationAuthService;

    public AuthServiceFactory(KeycloakAuthService keycloakAuthService, ApplicationAuthService applicationAuthService) {
        this.keycloakAuthService = keycloakAuthService;
        this.applicationAuthService = applicationAuthService;
    }

    public IAuthService getAuthService() {
        if (isKeycloakEnabled) {
            return keycloakAuthService;
        } else {
            return applicationAuthService;
        }
    }
}
