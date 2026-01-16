package com.proyect1.user.application.strategy.standard;

import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface StandardSpecificStrategy {

    Map<String, Object> getAllUsersEmail(String subscriptionId, Object filter, Pageable pageable);

    /**
     * Obtiene solo información de domicilio del usuario STANDARD
     */
    Map<String, Object> getUserStandardDomicilio(Long userId, String subscriptionId);

    // Map<String, Object> getUserEstablecimientoInfo(Long userId, String subscriptionId);
    // Map<String, Object> getUserHistoricalPayments(Long userId, String subscriptionId);
}