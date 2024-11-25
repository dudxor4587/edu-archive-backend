package com.backend.visitor.service;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {
    public static final String REDIS_KEY_TOTAL_VISITOR_COUNT = "VISITOR:TOTAL_COUNT";
    public static final String REDIS_KEY_MONTHLY_VISITOR_COUNT = "VISITOR:MONTHLY_COUNT:";
    public static final String REDIS_KEY_VISITOR = "VISITOR:";

    private final StringRedisTemplate redisTemplate;

    public boolean isVisitorExist(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public Long getVisitorCount(String range) {
        String countKey = range.equalsIgnoreCase("TOTAL") ?
                REDIS_KEY_TOTAL_VISITOR_COUNT :
                REDIS_KEY_MONTHLY_VISITOR_COUNT + range;
        String count = redisTemplate.opsForValue().get(countKey);
        return count != null ? Long.parseLong(count) : 0L;
    }

    public void deleteVisitorCountMonth(String month) {
        redisTemplate.delete(REDIS_KEY_MONTHLY_VISITOR_COUNT + month);
    }

    public void incrementVisitorCount(String key) {
        redisTemplate.opsForValue().increment(key);
    }

    public void setVisitorKey(String key, String value, long timeoutSeconds) {
        redisTemplate.opsForValue().set(key, value, timeoutSeconds, TimeUnit.SECONDS);
    }

    public void updateVisitorCount(String range, Long visitorCount) {
        String countKey = range.equalsIgnoreCase("TOTAL") ?
                REDIS_KEY_TOTAL_VISITOR_COUNT :
                REDIS_KEY_MONTHLY_VISITOR_COUNT + range;
        redisTemplate.opsForValue().set(countKey, visitorCount.toString());
    }
}
