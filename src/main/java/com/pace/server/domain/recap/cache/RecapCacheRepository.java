package com.pace.server.domain.recap.cache;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pace.server.domain.recap.dto.RecapResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Redis 기반 리캡 데이터 캐시 Repository
 * Key 형식: recap:{userId}:{date}
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class RecapCacheRepository {

    private static final String KEY_PREFIX = "recap:";
    private static final Duration TTL = Duration.ofHours(6);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 캐시에서 리캡 데이터 조회
     */
    public Optional<RecapResponse> findByUserIdAndDate(Long userId, LocalDate date) {
        String key = buildKey(userId, date);
        String json = redisTemplate.opsForValue().get(key);

        if (json == null) {
            log.debug("Cache miss for key: {}", key);
            return Optional.empty();
        }

        try {
            RecapResponse data = objectMapper.readValue(json, RecapResponse.class);
            log.debug("Cache hit for key: {}", key);
            return Optional.of(data);
        } catch (Exception e) {
            log.error("Failed to deserialize recap data for key: {}", key, e);
            return Optional.empty();
        }
    }

    /**
     * 캐시에 리캡 데이터 저장
     */
    public void save(Long userId, LocalDate date, RecapResponse data) {
        String key = buildKey(userId, date);

        try {
            String json = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(key, json, TTL);
            log.debug("Cached recap data for key: {}", key);
        } catch (Exception e) {
            log.error("Failed to cache recap data for key: {}", key, e);
        }
    }

    /**
     * 캐시 삭제
     */
    public void evict(Long userId, LocalDate date) {
        String key = buildKey(userId, date);
        redisTemplate.delete(key);
        log.debug("Evicted cache for key: {}", key);
    }

    private String buildKey(Long userId, LocalDate date) {
        return KEY_PREFIX + userId + ":" + date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
