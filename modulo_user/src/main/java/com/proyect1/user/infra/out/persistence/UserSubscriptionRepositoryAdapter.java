package com.proyect1.user.infra.out.persistence;

import com.proyect1.user.infra.out.persistence.repository.UserSubscriptionRepository;
import com.proyect1.user.ports.out.UserSubscriptionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserSubscriptionRepositoryAdapter implements UserSubscriptionRepositoryPort {

    private final UserSubscriptionRepository jpaRepository;

    @Override
    public boolean existsByUserIdAndSuscriptionIdAndIsActiveTrue(Long userId, String subscriptionId) {
        return jpaRepository.existsByUserIdAndIdSuscripcionAndIsActiveTrue(userId, subscriptionId);
    }

}
