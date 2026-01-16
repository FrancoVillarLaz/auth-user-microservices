package com.proyect1.user.domain.service;

import com.proyect1.user.ports.out.UserSubscriptionRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserSubscriptionDomainService {

    private final UserSubscriptionRepositoryPort userSubscriptionRepositoryPort;


    /**
     * Verifica que el usuario esté vinculado a una suscripción activa.
     */
    public void validateUserSubscription(Long userId, String subscriptionId) {
        boolean exists = userSubscriptionRepositoryPort
                .existsByUserIdAndSuscriptionIdAndIsActiveTrue(userId, subscriptionId);

        if (!exists) {
            throw new IllegalArgumentException(
                    String.format("El usuario %d no tiene la suscripción %s activa", userId, subscriptionId)
            );
        }
    }
}
