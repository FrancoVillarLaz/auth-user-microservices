package com.proyect1.user.application.command.standard;

import com.proyect1.user.domain.model.standard.UserStandard;

public record UpdateStandardCommand(
        String nombre,
        String apellido,
        String internalType,
        String numeroDocumento,
        Long establecimientoId,
        String manzana,
        String lote,
        String telefono,
        String correo,
        Boolean isActive
) {

    public UserStandard toDomain() {
        return UserStandard.builder()
                .nombre(nombre)
                .apellido(apellido)
                .internalType(internalType)
                .numeroDocumento(numeroDocumento)
                .establecimientoId(establecimientoId)
                .manzana(manzana)
                .lote(lote)
                .telefono(telefono)
                .correo(correo)
                .isActive(isActive)
                .build();
    }
}