package com.theanh.dev.IAM_Service.Services.Blacklist;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class JwtBlacklistService {
    private final String BLACKLIST_KEY = "jwt_blacklist";
    private RedisTemplate<String, String> redisTemplate;

    public void addToBlacklist(String token) {
        redisTemplate.opsForSet().add(BLACKLIST_KEY, token);
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(BLACKLIST_KEY, token));
    }

}
