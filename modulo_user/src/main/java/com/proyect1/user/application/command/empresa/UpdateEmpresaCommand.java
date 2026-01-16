package com.proyect1.user.application.command.empresa;

import com.proyect1.user.domain.model.empresa.UserEmpresa;
import com.proyect1.user.domain.model.empresa.CondicionFrenteIva;

public record UpdateEmpresaCommand(
        String razonSocial,
        String cuit,
        String telefono,
        String condicionIvaId,
        Boolean isActive
) {

    public UserEmpresa toDomain() {
        CondicionFrenteIva condicionFrenteIva = null;
        if (condicionIvaId != null && !condicionIvaId.trim().isEmpty()) {
            condicionFrenteIva = CondicionFrenteIva.builder()
                    .id(condicionIvaId)
                    .build();
        }

        return UserEmpresa.builder()
                .razonSocial(razonSocial)
                .cuit(cuit)
                .telefono(telefono)
                .CondicionFrenteIva(condicionFrenteIva)
                .isActive(isActive)
                .build();
    }
}