package com.proyect1.user.shared.exception.auth;

/**
 * Excepción express
 */
public class MissingAuthFieldException extends InvalidAuthEventException {

    public MissingAuthFieldException(String fieldName) {
        super("Falta el campo requerido en evento Auth: " + fieldName);
    }
}
