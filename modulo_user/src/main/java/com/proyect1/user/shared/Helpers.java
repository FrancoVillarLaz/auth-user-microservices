package com.proyect1.user.shared;

import com.proyect1.user.application.mapper.UserFilterEmpresaMapper;
import com.proyect1.user.application.mapper.UserFilterStandardMapper;
import com.proyect1.user.shared.exception.SubscriptionNotSupportedException;

import java.util.Map;

public class Helpers {


    /**
     * Helper method para construir el filtro apropiado según el tipo de suscripción
     */
    public static Object buildFilterFromParams(String subscriptionId, Map<String, String> params) {
        // Normalizar subscriptionId
        String normalizedSub = subscriptionId.toUpperCase();

        return switch (normalizedSub) {
            case "INNCOME" -> UserFilterStandardMapper.fromParams(subscriptionId, params);
            case "EMPRESA" -> UserFilterEmpresaMapper.fromParams(subscriptionId, params);
            default -> throw new SubscriptionNotSupportedException(
                    "Subscription type not supported: " + subscriptionId
            );
        };
    }
}
