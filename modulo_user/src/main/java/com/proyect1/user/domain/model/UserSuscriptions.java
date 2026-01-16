package com.proyect1.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Modelo de dominio - Usuario standard
 * POJO puro sin anotaciones de JPA
 * se deberia sacar las anotaciones lombok tambien
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSuscriptions {
    private Long id;
    private Long userId;
    private String subscriptionId;
    private Boolean isActive;
    public LocalDateTime createdAt = LocalDateTime.now();
    public LocalDateTime updatedAt = LocalDateTime.now();

}
