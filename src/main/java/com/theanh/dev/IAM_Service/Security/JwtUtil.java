package com.theanh.dev.IAM_Service.Security;

import com.theanh.dev.IAM_Service.Exception.AppException;
import com.theanh.dev.IAM_Service.Exception.ErrorCode;
import com.theanh.dev.IAM_Service.Models.Users;
import com.theanh.dev.IAM_Service.Repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtUtil {

    RSAKeyUtil rsaKeyUtil;
    UserRepository userRepository;
    static long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 30;
    static long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 5;

    public String generateToken(UserDetails userDetails, long expirationTime) throws Exception {
        PrivateKey privateKey = rsaKeyUtil.getPrivateKey();
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .setId(UUID.randomUUID().toString())
                .claim("role", buildScope(userDetails))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    private String buildScope(UserDetails userDetails) {
        StringJoiner roleJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(userDetails.getAuthorities())) {
            userDetails.getAuthorities().forEach(authority -> {
                roleJoiner.add(authority.getAuthority());
            });
        }

        return roleJoiner.toString();
    }

    public String generateAccessToken(UserDetails userDetails) throws Exception {
        return generateToken(userDetails, ACCESS_TOKEN_EXPIRATION);
    }

    public String generateRefreshToken(UserDetails userDetails) throws Exception {
        return generateToken(userDetails, REFRESH_TOKEN_EXPIRATION);
    }

    public Claims extractClaims(String token) throws Exception {
        try {
            PublicKey publicKey = rsaKeyUtil.getPublicKey();
            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String extractEmail(String token) {
        try {
            return extractClaims(token).getSubject();
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    public long getExpirationTime(String token) {
        try {
            return extractClaims(token).getExpiration().getTime();
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            return extractClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
