package com.proyect1.user.shared.exception;

/**
 * Excepción express
 */
public class SubscriptionNotSupportedException extends RuntimeException {

    public SubscriptionNotSupportedException(String subscriptionId) {
        super("Suscripción no soportada: " + subscriptionId);
    }
}