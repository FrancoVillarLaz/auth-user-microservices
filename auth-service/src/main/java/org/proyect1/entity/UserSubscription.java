package org.proyect1.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "user_subscriptions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "id_suscripcion"}))
public class UserSubscription extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "user_id", nullable = false)
    public Long userId;

    @Column(name = "id_suscripcion", nullable = false)
    public String idSuscripcion;

    @Column(name = "is_active", nullable = false)
    public boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    public LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /** ✅ Devuelve todas las suscripciones activas del usuario */
    public static List<String> getActiveSubscriptions(Long userId) {
        return find("userId = ?1 and isActive = true", userId)
                .stream()
                .map(u -> ((UserSubscription) u).idSuscripcion)
                .toList();
    }


    public static boolean hasSubscriptionActive(Long userId, String subscriptionId) {
        return find("userId = ?1 AND idSuscripcion = ?2 AND isActive = true",
                userId, subscriptionId
        ).count() > 0;
    }

    public static boolean hasSubscriptionDesactive(Long userId, String subscriptionId) {
        return find("userId = ?1 AND idSuscripcion = ?2 AND isActive = false",
                userId, subscriptionId
        ).count() > 0;
    }
    public static Optional<UserSubscription> findByUserAndSub(Long userId, String subscriptionId) {
        return find("userId = ?1 AND idSuscripcion = ?2", userId, subscriptionId).firstResultOptional();
    }

    public static void activate(Long userId, String subscriptionId) {
        findByUserAndSub(userId, subscriptionId).ifPresent(sub -> {
            sub.isActive = true;
            sub.persist();
        });
    }

    public static void deactivate(Long userId, String subscriptionId) {
        findByUserAndSub(userId, subscriptionId).ifPresent(sub -> {
            sub.isActive = false;
            sub.persist();
        });
    }
}
