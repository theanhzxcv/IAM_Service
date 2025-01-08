package com.theanh.dev.IAM_Service.Jwt;

import com.nimbusds.jwt.JWT;
import com.theanh.dev.IAM_Service.Exception.AppException;
import com.theanh.dev.IAM_Service.Exception.ErrorCode;
import com.theanh.dev.IAM_Service.Repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtDecoder jwtDecoder;
    private final RSAKeyUtil rsaKeyUtil;
    private final UserRepository userRepository;
    private final static long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 30;
    private final static long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 5;

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

    public String extractEmailSystemJwt(String token) {
        try {
            return extractClaims(token).getSubject();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String extractEmailKeycloakJwt(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getClaim("email");
    }

    public Date getSystemJwtExpirationTime(String token) {
        try {
            return extractClaims(token).getExpiration();
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    public Date getKeycloakJwtExpirationTime(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        Instant expiration = jwt.getExpiresAt();
        return Date.from(expiration);
    }

    private boolean isTokenExpired(String token) {
        try {
            return extractClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    public boolean isKeycloakTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmailKeycloakJwt(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isSystemTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmailSystemJwt(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
