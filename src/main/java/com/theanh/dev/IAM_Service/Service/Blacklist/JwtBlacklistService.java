package com.theanh.dev.IAM_Service.Service.Blacklist;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtBlacklistService {

    String BLACKLIST_KEY = "jwt_blacklist";

    private RedisTemplate<String, String> redisTemplate;

    // Add JWT to blacklist
    public void addToBlacklist(String token, long expirationTime) {
        long ttl = expirationTime - System.currentTimeMillis();
        if (ttl > 0) {
            redisTemplate.opsForSet().add(BLACKLIST_KEY, token, String.valueOf(ttl));
        }
    }

    // Check if JWT is blacklisted
    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(BLACKLIST_KEY, token));
    }
}
