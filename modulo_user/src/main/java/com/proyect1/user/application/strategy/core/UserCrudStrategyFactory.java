package com.proyect1.user.application.strategy.core;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserCrudStrategyFactory {

    private final List<UserCrudStrategy> strategies;

    public UserCrudStrategy getStrategy(String subscriptionId) {
        return strategies.stream()
                .filter(s -> subscriptionId.equals(s.getSupportedSubscriptionId(subscriptionId)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Estrategia no encontrada para: " + subscriptionId
                ));
    }
}