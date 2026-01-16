package com.proyect1.user.shared.exception.auth;


/**
 * Excepción express
 */
public class InvalidAuthFieldException extends InvalidAuthEventException {

    public InvalidAuthFieldException(String fieldName) {
        super("Campo inválido en evento Auth: " + fieldName);
    }
}
