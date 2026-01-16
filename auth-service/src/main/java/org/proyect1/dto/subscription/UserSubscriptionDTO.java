package org.proyect1.dto.subscription;

import org.proyect1.entity.UserSubscription;

import java.time.LocalDateTime;

public record UserSubscriptionDTO(
        Long id,
        Long userId,
        String idSuscripcion,
        boolean isActive,
        LocalDateTime createdAt
) {
    public static UserSubscriptionDTO fromEntity(UserSubscription entity) {
        return new UserSubscriptionDTO(
                entity.id,
                entity.userId,
                entity.idSuscripcion,
                entity.isActive,
                entity.createdAt
        );
    }
}