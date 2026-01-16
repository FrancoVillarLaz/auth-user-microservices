package org.proyect1.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "users")
public class User extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(unique = true, nullable = false)
    public String username;

    @Column(unique = true, nullable = false)
    public String email;

    @Column(nullable = false)
    public String password;

    @Column(nullable = false)
    public String role = "USER";

    @Enumerated(EnumType.STRING)
    public UserLastModification lastModification;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at")
    public LocalDateTime updatedAt;

    @Column(name = "is_active")
    public Boolean isActive = true;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static Optional<User> findByUsername(String username) {
        return find("username", username).firstResultOptional();
    }

    public static Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public static Optional<User> findByUsernameOrEmail(String identifier) {
        return find("username = ?1 or email = ?1", identifier).firstResultOptional();
    }

    public static List<String> findActiveIdsByUser(Long userId) {
        return getEntityManager()
                .createQuery("SELECT u.idSuscripcion FROM UserSubscription u WHERE u.userId = :userId AND u.isActive = true", String.class)
                .setParameter("userId", userId)
                .getResultList();
    }


}
