package org.proyect1.dto.subscription.response;

public class SubscriptionCheckResponse {

    public Long userId;
    public String idSuscripcion;
    public boolean hasAccess;

    public SubscriptionCheckResponse(Long userId, String idSuscripcion, boolean hasAccess) {
        this.userId = userId;
        this.idSuscripcion = idSuscripcion;
        this.hasAccess = hasAccess;
    }
}
