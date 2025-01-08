package com.theanh.dev.IAM_Service.Services.ServiceImp.Management;

import com.theanh.dev.IAM_Service.Config.MyAuditorAware;
import com.theanh.dev.IAM_Service.Dtos.Requests.Admin.UserCreateRequest;
import com.theanh.dev.IAM_Service.Dtos.Requests.Admin.UserSearchRequest;
import com.theanh.dev.IAM_Service.Dtos.Requests.Admin.UserUpdateRequest;
import com.theanh.dev.IAM_Service.Dtos.Response.Management.UserResponse;
import com.theanh.dev.IAM_Service.Exception.AppException;
import com.theanh.dev.IAM_Service.Exception.ErrorCode;
import com.theanh.dev.IAM_Service.Mapper.UserMapper;
import com.theanh.dev.IAM_Service.Models.Users;
import com.theanh.dev.IAM_Service.Repositories.RoleRepository;
import com.theanh.dev.IAM_Service.Repositories.UserRepository;
import com.theanh.dev.IAM_Service.Services.IManagementService;
import com.theanh.dev.IAM_Service.Services.ServiceImp.Tfa.TwoFactorAuthenticationService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ManagementService implements IManagementService {
    @PersistenceContext
    private EntityManager entityManager;
    private final UserMapper userMapper;
    private final MyAuditorAware myAuditorAware;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TwoFactorAuthenticationService tfaService;

    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;
    @Value("${keycloak.client.id}")
    private String clientId;
    @Value("${keycloak.client.secret}")
    private String clientSecret;

    private String getAdminToken() {
        String adminUsername = "theanh";
        String adminPassword = "theanh";
        String tokenUrl = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("username", adminUsername);
        body.add("password", adminPassword);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(
                tokenUrl,
                new org.springframework.http.HttpEntity<>(body, headers),
                Map.class
        );

        return (String) response.getBody().get("access_token");
    }

    private void saveUserToKeycloak(String username,
                                    String email,
                                    String firstname,
                                    String lastname,
                                    String password) {
        String adminToken = getAdminToken();
        String registerUrl = authServerUrl + "/admin/realms/" + realm + "/users";

        Map<String, Object> userPayload = Map.of(
                "username", username,
                "email", email,
                "firstName", firstname,
                "lastName", lastname,
                "enabled", true,
                "credentials", List.of(
                        Map.of(
                                "type", "password",
                                "value", password,
                                "temporary", false
                        )
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> responseBody = restTemplate.postForEntity(
                    registerUrl,
                    new HttpEntity<>(userPayload, headers),
                    String.class
            );

            if (!responseBody.getStatusCode().is2xxSuccessful()) {
                throw new AppException(ErrorCode.REGISTER_FAIL);
            }

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 409) {
                throw new AppException(ErrorCode.EXISTED_USER);
            }
        }
    }

    @Override
    public UserResponse createUser(UserCreateRequest userCreateRequest) {
        if (userRepository.findByEmail(userCreateRequest.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.EXISTED_USER);
        }

        Users user = userMapper.toUser(userCreateRequest);
        saveUserToKeycloak(userCreateRequest.getEmail(),
                userCreateRequest.getEmail(),
                userCreateRequest.getFirstname(),
                userCreateRequest.getLastname(),
                userCreateRequest.getPassword());
        user.setPassword(passwordEncoder.encode(userCreateRequest.getPassword()));
        user.setSecret(tfaService.generateSecretKey());
        user.setCreatedBy(myAuditorAware.getCurrentAuditor().orElse("Unknown"));
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        user.setLastModifiedBy(myAuditorAware.getCurrentAuditor().orElse("Unknown"));
        user.setLastModifiedAt(new Timestamp(System.currentTimeMillis()));

        var roles = roleRepository.findAllById(userCreateRequest.getRoles());
        user.setRoles(new HashSet<>(roles));
        user = userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    @Override
    public Page<UserResponse> getAllUsers(int page,
                                          int size,
                                          String sortBy,
                                          String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findAll(pageable)
                .map(userMapper::toUserResponse);
    }

    @Override
    public UserResponse getUserById(String id) {
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED_USER)));
    }

    @Override
    public Page<UserResponse> searchUserByKeyword(UserSearchRequest request) {
        Map<String, Object> params = new HashMap<>();
        String whereClause = createWhereQuery(request, params);


        String queryStr = "SELECT u FROM Users u " + whereClause;
        Query query = entityManager.createQuery(queryStr, Users.class);

        params.forEach(query::setParameter);

        Pageable pageable = PageRequest.of(request.getOffset() / request.getLimit(), request.getLimit());

        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());

        List<UserResponse> users = query.getResultList();

        // Get the total number of elements (needed for pagination)
        Query countQuery = entityManager.createQuery("SELECT COUNT(u) FROM Users u " + whereClause);
        params.forEach(countQuery::setParameter);
        long totalElements = (long) countQuery.getSingleResult();

        return new PageImpl<>(users, pageable, totalElements);
    }

    private String createWhereQuery(UserSearchRequest request, Map<String, Object> values) {
        StringBuilder sql = new StringBuilder();
        sql.append("where u.isDeleted = false");

        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            String keyword = "%" + request.getKeyword().trim().toLowerCase() + "%";

            sql.append(" AND (LOWER(u.email) LIKE :keyword ")
                    .append(" OR LOWER(u.firstname) LIKE :keyword ")
                    .append(" OR LOWER(u.lastname) LIKE :keyword) ");
            values.put("keyword", keyword);
        }
        return sql.toString();
    }

    @Override
    public UserResponse updateUser(UserUpdateRequest userUpdateRequest) {
        return null;
    }

    @Override
    public String banUser(String id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED_USER));

        if (user.isBanned()) {
            return "User has already been banned!";
        }
        user.setBanned(true);
        userRepository.save(user);

        return "User banned!";
    }

    @Override
    public String unBanUser(String id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED_USER));

        if (!user.isBanned()) {
            return "User hasn't been banned!";
        }
        user.setBanned(false);
        userRepository.save(user);

        return "User unbanned!";
    }

    @Override
    public String deleteUser(String id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED_USER));
        user.setDeleted(true);
        userRepository.save(user);

        return "User deleted!";
    }
}
