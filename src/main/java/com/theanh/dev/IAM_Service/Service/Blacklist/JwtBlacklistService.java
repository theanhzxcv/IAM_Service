package com.theanh.dev.IAM_Service.Service.Blacklist;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtBlacklistService {

    String BLACKLIST_KEY = "jwt_blacklist";

    private RedisTemplate<String, String> redisTemplate;

    public void addToBlacklist(String token) {
        redisTemplate.opsForSet().add(BLACKLIST_KEY, token);
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(BLACKLIST_KEY, token));
    }

}
