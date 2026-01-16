package com.proyect1.user.infra.out.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_subscriptions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "id_suscripcion"})
)
public class UserSubscriptionsEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "user_id", nullable = false)
        private Long userId;

        @Column(name = "id_suscripcion", nullable = false)
        private String idSuscripcion;

        @Column(name = "is_active", nullable = false)
        private boolean isActive = true;

        @Column(name = "created_at", nullable = false)
        private LocalDateTime createdAt = LocalDateTime.now();

        @Column(name = "updated_at")
        private LocalDateTime updatedAt = LocalDateTime.now();

        @PreUpdate
        public void preUpdate() {
            this.updatedAt = LocalDateTime.now();
        }

        public Long getId() { return id; }

        public Long getUserId() { return userId; }

        public void setUserId(Long userId) { this.userId = userId; }

        public String getIdSuscripcion() { return idSuscripcion; }

        public void setIdSuscripcion(String idSuscripcion) { this.idSuscripcion = idSuscripcion; }

        public boolean isActive() { return isActive; }

        public void setActive(boolean active) { isActive = active; }

        public LocalDateTime getCreatedAt() { return createdAt; }

        public LocalDateTime getUpdatedAt() { return updatedAt; }

        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

