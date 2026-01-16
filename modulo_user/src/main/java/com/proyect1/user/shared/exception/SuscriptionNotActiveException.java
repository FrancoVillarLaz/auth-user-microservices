package com.proyect1.user.shared.exception;

public class SuscriptionNotActiveException extends RuntimeException {
    public SuscriptionNotActiveException() {
        super("El usuario no esta suscripto o no tiene activa la suscripcion");
    }
}
