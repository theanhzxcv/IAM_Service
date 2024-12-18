package com.theanh.dev.IAM_Service.Security;

import com.theanh.dev.IAM_Service.Model.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final RSAKeyUtil rsaKeyUtil;

    public String generateToken(Users user) throws Exception {
        PrivateKey privateKey = rsaKeyUtil.getPrivateKey();
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuer("theanh.dev")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .signWith(SignatureAlgorithm.RS256, privateKey)
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

    public String extractUsername(String token) {
        try {
            return extractClaims(token).getSubject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }
}
