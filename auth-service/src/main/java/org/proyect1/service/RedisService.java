package org.proyect1.service;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class RedisService {

    @Inject
    RedisDataSource redisDataSource;

    @ConfigProperty(name = "jwt.refresh.token.expiration")
    Long refreshTokenExpiration;

    private ValueCommands<String, String> commands;

    public void init() {
        if (commands == null) {
            commands = redisDataSource.value(String.class);
        }
    }

    public void saveRefreshToken(Long userId, String refreshToken) {
        init();
        String key = "refresh_token:" + userId;
        commands.setex(key, refreshTokenExpiration, refreshToken);
    }

    public String getRefreshToken(String userId) {
        init();
        String key = "refresh_token:" + userId;
        return commands.get(key);
    }

    public void deleteRefreshToken(String userId) {
        init();
        String key = "refresh_token:" + userId;
        redisDataSource.key().del(key);
    }

    public void blacklistToken(String token, Long expirationSeconds) {
        init();
        String key = "blacklist:" + token;
        commands.setex(key, expirationSeconds, "true");
    }

    public boolean isTokenBlacklisted(String token) {
        init();
        String key = "blacklist:" + token;
        return commands.get(key) != null;
    }
}