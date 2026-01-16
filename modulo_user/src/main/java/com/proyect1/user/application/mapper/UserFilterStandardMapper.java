package com.proyect1.user.application.mapper;

import com.proyect1.user.domain.model.standard.UserFilterStandard;
import com.proyect1.user.infra.in.web.dto.Request.UserFilterStandardRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

public final class UserFilterStandardMapper {

    private UserFilterStandardMapper() {}

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Convierte DTO a modelo de dominio
     */
    public static UserFilterStandard toDomain(UserFilterStandardRequest dto) {
        return new UserFilterStandard(
                dto.subscriptionId(),
                dto.userId(),
                dto.nombre(),
                dto.apellido(),
                dto.numeroDocumento(),
                dto.correo(),
                dto.internalType(),
                dto.establecimientoId(),
                dto.manzana(),
                dto.lote(),
                dto.telefono(),
                dto.isActive(),
                dto.createdAtStart(),
                dto.createdAtEnd(),
                dto.updatedAtFrom(),
                dto.updatedAtTo(),
                dto.pageNumber(),
                dto.pageSize(),
                dto.sortBy(),
                dto.sortDirection()
        );
    }

    /**
     * Convierte Map de parámetros a modelo de dominio
     * Usado cuando los parámetros vienen como query params
     */
    public static UserFilterStandard fromParams(String subscriptionId, Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return new UserFilterStandard(
                    subscriptionId,
                    null, null, null, null, null, null,
                    null, null, null, null, null,
                    null, null, null, null,
                    null, null, null, null
            );
        }

        return new UserFilterStandard(
                subscriptionId,
                parseLong(params.get("userId")),
                params.get("nombre"),
                params.get("apellido"),
                params.get("numeroDocumento"),
                params.get("correo"),
                parseList(params.get("internalType")),
                parseLong(params.get("establecimientoId")),
                params.get("manzana"),
                params.get("lote"),
                params.get("telefono"),
                parseBoolean(params.get("isActive")),
                parseDateTime(params.get("createdAtStart")),
                parseDateTime(params.get("createdAtEnd")),
                parseDateTime(params.get("updatedAtFrom")),
                parseDateTime(params.get("updatedAtTo")),
                parseInteger(params.get("pageNumber")),
                parseInteger(params.get("pageSize")),
                params.get("sortBy"),
                params.get("sortDirection")
        );
    }

    // ============ Métodos auxiliares para parsing seguro ============

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

    private static Integer parseInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Boolean parseBoolean(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Boolean.parseBoolean(value.trim());
    }

    private static LocalDateTime parseDateTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value.trim(), FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static List<String> parseList(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            // Asume formato: "VALOR1,VALOR2,VALOR3"
            return List.of(value.trim().split("\\s*,\\s*"));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Método para crear un filtro por userId (caso común)
     */
    public static UserFilterStandard fromUserId(String subscriptionId, Long userId) {
        return new UserFilterStandard(
                subscriptionId,
                userId,
                null, null,null, null, null,
                null, null, null, null, null,
                null, null, null, null,
                null, null, null, null
        );
    }

    /**
     * Método para crear un filtro por documento (caso común)
     */
    public static UserFilterStandard fromDocumento(String subscriptionId, String documento) {
        return new UserFilterStandard(
                subscriptionId,
                null,
                null,
                null,
                documento,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
}