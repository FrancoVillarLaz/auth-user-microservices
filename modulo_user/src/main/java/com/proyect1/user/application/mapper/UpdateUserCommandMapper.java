package com.proyect1.user.application.mapper;

import com.proyect1.user.infra.in.web.dto.Request.UpdateUserRequest;
import com.proyect1.user.application.command.standard.UpdateStandardCommand;

public final class UpdateUserCommandMapper {

    private UpdateUserCommandMapper() {}

    public static UpdateStandardCommand fromRequest(UpdateUserRequest request) {
        return new UpdateStandardCommand(
                request.getNombre(),
                request.getApellido(),
                request.getInternalType(),
                request.getNumeroDocumento(),
                request.getEstablecimientoId(),
                request.getManzana(),
                request.getLote(),
                request.getTelefono(),
                null,
                request.getIsActive()
        );
    }
}
