package com.proyect1.user.infra.out.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyect1.user.ports.out.CachePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

/**
 * Adapter OUT - Implementa el puerto de cache con Redis
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCacheAdapter implements CachePort {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<Map<String, Object>> get(String key) {
        try {
            String cached = redisTemplate.opsForValue().get(key);

            if (cached == null) {
                return Optional.empty();
            }

            Map<String, Object> data = objectMapper.readValue(
                    cached,
                    new TypeReference<Map<String, Object>>() {}
            );

            return Optional.of(data);

        } catch (Exception e) {
            log.warn("Error reading from cache: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void put(String key, Map<String, Object> value, long ttlSeconds) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json, Duration.ofSeconds(ttlSeconds));
            log.debug("Cached data with key: {}", key);

        } catch (Exception e) {
            log.error("Error writing to cache: {}", e.getMessage());
        }
    }

    @Override
    public void invalidate(String key) {
        try {
            redisTemplate.delete(key);
            log.debug("key invalida cache{}", key);

        } catch (Exception e) {
            log.error("Error cache invalidado: {}", e.getMessage());
        }
    }

    @Override
    public void invalidatePattern(String pattern) {
        try {
            // Nota: En producción para buscar en produccion se utiliza scan que anda mas rapido y no bloquea
            var keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.debug("keys {} no marchean el patron: {}", keys.size(), pattern);
            }

        } catch (Exception e) {
            log.error("Error invalidando el patron: {}", e.getMessage());
        }
    }
}