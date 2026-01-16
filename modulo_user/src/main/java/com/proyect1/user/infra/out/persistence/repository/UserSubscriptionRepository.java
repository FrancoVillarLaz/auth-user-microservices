package com.proyect1.user.infra.out.persistence.repository;


import com.proyect1.user.infra.out.persistence.entity.UserSubscriptionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscriptionsEntity, Long> {

    boolean existsByUserIdAndIdSuscripcionAndIsActiveTrue(Long userId, String subscriptionId);


}
