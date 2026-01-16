package com.proyect1.user.domain.model.empresa;


public enum CondicionesIva {
    MONOTRIBUTO,
    IVA_RESP_INSCRIPTO,
    IVA_EXENTO,
    CONDUMIDOR;

    /**
     * Convierte un String a EventType de forma segura.
     */
    public static CondicionesIva fromString(String condicionName) {

        try {
            return CondicionesIva.valueOf(condicionName.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Tipo de evento no reconocido: " + condicionName);

            throw new IllegalArgumentException("Tipo de evento desconocido: " + condicionName);
        }
    }

}
