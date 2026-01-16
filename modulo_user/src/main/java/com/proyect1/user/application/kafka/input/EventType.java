package com.proyect1.user.application.kafka.input;

public enum EventType {
    USER_REGISTERED,
    USER_LOGGED_IN,
    USER_LOGGED_OUT,
    TOKEN_REFRESHED,
    LOGIN_FAILED,
    REGISTRATION_FAILED,
    USER_CREATION_SUCCESS,
    USER_CREATION_FAILED;

    /**
     * Convierte un String a EventType de forma segura.
     */
    public static EventType fromString(String eventTypeName) {

        try {
            return EventType.valueOf(eventTypeName.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Tipo de evento no reconocido: " + eventTypeName);

            throw new IllegalArgumentException("Tipo de evento desconocido: " + eventTypeName);
        }
    }
}

