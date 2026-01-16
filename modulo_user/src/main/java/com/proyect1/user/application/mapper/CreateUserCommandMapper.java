package com.proyect1.user.application.mapper;

import com.proyect1.user.application.command.standard.CreateStandardCommand;
import com.proyect1.user.infra.in.web.dto.Request.CreateUserRequest;

public final class CreateUserCommandMapper {

    private CreateUserCommandMapper() {}

    public static CreateStandardCommand fromRequest(CreateUserRequest request) {
        return new CreateStandardCommand(
                request.getUserId(),
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