package com.theanh.dev.IAM_Service.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtUtil {

    RSAKeyUtil rsaKeyUtil;

    public String generateAccessToken(UserDetails userDetails) throws Exception {
        PrivateKey privateKey = rsaKeyUtil.getPrivateKey();
        long accessTokenExpiration = 1800000;
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .setId(UUID.randomUUID().toString())
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) throws Exception {
        PrivateKey privateKey = rsaKeyUtil.getPrivateKey();
        long refreshTokenExpiration = 18000000;
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public Claims extractClaims(String token) throws Exception {
        PublicKey publicKey = rsaKeyUtil.getPublicKey();

        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractEmail(String token) {
        try {
            return extractClaims(token).getSubject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long getExpirationTime(String token) {
        try {
            return extractClaims(token).getExpiration().getTime();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            return extractClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractEmail(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
