package com.proyect1.user.application.command.empresa;

import com.proyect1.user.domain.model.empresa.CondicionFrenteIva;
import com.proyect1.user.domain.model.empresa.UserEmpresa;

public record CreateEmpresaCommand(
        Long userId,
        String internalType,
        String razonSocial,
        String cuit,
        String telefono,
        String condicionIvaId,
        Boolean isActive
) {

    public UserEmpresa toDomain() {

        // Crear CondicionFrenteIva si hay ID
        CondicionFrenteIva condicionFrenteIva = null;
        if (condicionIvaId != null && !condicionIvaId.trim().isEmpty()) {
            condicionFrenteIva = CondicionFrenteIva.builder()
                    .id(condicionIvaId)
                    .build();
        }

        return UserEmpresa.builder()
                .userAuthId(userId)
                .internalType(internalType)
                .razonSocial(razonSocial)
                .cuit(cuit)
                .telefono(telefono)
                .CondicionFrenteIva(condicionFrenteIva)
                .isActive(isActive != null ? isActive : true)
                .build();
    }
}