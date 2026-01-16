package org.proyect1.dto.subscription.response;


import org.proyect1.dto.subscription.UserSubscriptionDTO;

public record SubscriptionCreatedResponse(
        String status,
        String description,
        UserSubscriptionDTO subscription
) {public static SubscriptionCreatedResponse created(UserSubscriptionDTO sub) {
    return new SubscriptionCreatedResponse(
            "CREATED",
            "Suscripción creada exitosamente.",
            sub
    );
}

    public static SubscriptionCreatedResponse reactivated(UserSubscriptionDTO sub) {
        return new SubscriptionCreatedResponse(
                "REACTIVATED",
                "Suscripción existente reactivada.",
                sub
        );
    }

    public static SubscriptionCreatedResponse alreadyActive(UserSubscriptionDTO sub) {
        return new SubscriptionCreatedResponse(
                "ALREADY_ACTIVE",
                "La suscripción ya se encontraba activa.",
                sub
        );
    }

    public static SubscriptionCreatedResponse error(String detailedMessage) {
        return new SubscriptionCreatedResponse(
                "ERROR",
                "Error al procesar la solicitud: " + detailedMessage,
                null
        );
}
}