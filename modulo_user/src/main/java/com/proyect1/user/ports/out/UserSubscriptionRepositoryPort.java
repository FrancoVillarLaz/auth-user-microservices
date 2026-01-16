package com.proyect1.user.ports.out;


public interface UserSubscriptionRepositoryPort {
    boolean existsByUserIdAndSuscriptionIdAndIsActiveTrue(Long userId, String subscriptionId);

;
}
