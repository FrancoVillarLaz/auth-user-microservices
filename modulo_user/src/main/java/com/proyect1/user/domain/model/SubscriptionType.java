package com.proyect1.user.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum de dominio para tipos de suscripción
 * Define el mapeo entre suscripción y tabla
 */
@Getter
@RequiredArgsConstructor
public enum SubscriptionType {

    STANDARD("STANDARD", "user_standard"),
    EMPRESA("EMPRESA", "user_empresa");

    private final String subscriptionId;
    private final String tableName;

    /**
     * Obtiene el tipo por ID de suscripción
     */
    public static SubscriptionType fromSubscriptionId(String subscriptionId) {
        for (SubscriptionType type : values()) {
            if (type.subscriptionId.equals(subscriptionId)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Suscripción no soportada: " + subscriptionId);
    }

    /**
     * Verifica si una suscripción es válida
     */
    public static boolean isValidSub(String subscriptionId) {
        try {
            fromSubscriptionId(subscriptionId);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}