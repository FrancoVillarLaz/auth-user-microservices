package com.proyect1.user.application.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.proyect1.user.application.kafka.input.AuthEvent;
import com.proyect1.user.domain.model.empresa.CondicionFrenteIva;
import com.proyect1.user.domain.model.standard.UserStandard;
import com.proyect1.user.domain.model.empresa.UserEmpresa;
import lombok.extern.slf4j.Slf4j;

import static com.proyect1.user.shared.json.AuthEventValidator.*;

@Slf4j
public record CreateUserFromAuthCommand(
        String eventId,
        Long userId,
        String username,
        String email,
        String ipAddress,

        // Campos comunes
        String subscriptionId, // "STANDARD" o "EMPRESA" - agregado para determinar el tipo
        boolean isActive,

        // Campos específicos de Standard
        String nombre,
        String apellido,
        String internalType,
        String numeroDocumento,
        Long establecimientoId,
        String manzana,
        String lote,
        String telefono,

        // Campos específicos de EMPRESA
        String razonSocial,
        String cuit,
        String condicionIvaId
) {
    public static CreateUserFromAuthCommand from(AuthEvent event) {
        JsonNode meta = event.userMetaData();
        log.debug("Creando CreateUserFromAuthCommand desde AuthEvent con metadatos: {}", meta);

        String userType = event.SuscriptionId();
        log.debug("Tipo de usuario: {}", userType);

        return new CreateUserFromAuthCommand(
                event.eventId(),
                event.userId(),
                event.username(),
                event.email(),
                event.ipAddress(),
                userType,
                optionalBoolean(meta, "isActive"),

                // Campos Standard
                optionalText(meta, "nombre"),
                optionalText(meta, "apellido"),
                optionalText(meta, "internalType"),
                optionalText(meta, "numeroDocumento"),
                parseLong(optionalText(meta, "establecimientoId")),
                optionalText(meta, "manzana"),
                optionalText(meta, "lote"),
                optionalText(meta, "telefono"),

                // Campos EMPRESA
                optionalText(meta, "razonSocial"),
                optionalText(meta, "cuit"),
                optionalText(meta, "condicionIvaId")
        );
    }

    public UserStandard toStandardDomain() {
        if (!"STANDARD".equalsIgnoreCase(subscriptionId)) {
            throw new IllegalStateException("No se puede crear dominio Standard para tipo: " + subscriptionId);
        }

        return UserStandard.builder()
                .apellido(this.apellido)
                .nombre(this.nombre)
                .establecimientoId(this.establecimientoId)
                .internalType(this.internalType)
                .isActive(this.isActive)
                .lote(this.lote)
                .manzana(this.manzana)
                .numeroDocumento(this.numeroDocumento)
                .telefono(this.telefono)
                .userId(this.userId)
                .correo(this.email)
                .build();
    }

    public UserEmpresa toEmpresaDomain() {
        if (!"EMPRESA".equalsIgnoreCase(subscriptionId)) {
            throw new IllegalStateException("No se puede crear dominio EMPRESA para tipo: " + subscriptionId);
        }

        CondicionFrenteIva condicionFrenteIva = null;
        if (condicionIvaId != null && !condicionIvaId.trim().isEmpty()) {
            condicionFrenteIva = CondicionFrenteIva.builder()
                    .id(condicionIvaId)
                    .build();
        }

        return UserEmpresa.builder()
                .internalType(internalType)
                .userAuthId(userId)
                .razonSocial(razonSocial)
                .cuit(cuit)
                .telefono(telefono)
                .correo(email)
                .CondicionFrenteIva(condicionFrenteIva)
                .isActive(isActive)
                .build();
    }

    /**
     * Método para determinar si es STANDARD
     */
    public boolean isStandard() {
        return "STANDARD".equalsIgnoreCase(subscriptionId);
    }

    /**
     * Método para determinar si es EMPRESA
     */
    public boolean isEmpresa() {
        return "EMPRESA".equalsIgnoreCase(subscriptionId);
    }

    private static Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}