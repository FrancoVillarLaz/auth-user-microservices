package com.proyect1.user.application.mapper;

import com.proyect1.user.domain.model.empresa.UserFilterEmpresa;
import com.proyect1.user.infra.in.web.dto.Request.UserFilterEmpresaRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

public final class UserFilterEmpresaMapper {

    private UserFilterEmpresaMapper() {}

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Convierte DTO a modelo de dominio
     */
    public static UserFilterEmpresa toDomain(UserFilterEmpresaRequest dto) {
        return new UserFilterEmpresa(
                dto.getSubscriptionId(),
                dto.getCorreo(),
                dto.getCuit(),
                dto.getRazonSocial(),
                dto.getUserId(),
                dto.getTelefono(),
                dto.getIsActive(),
                dto.getCreatedAtFrom(),
                dto.getCreatedAtTo(),
                dto.getUpdatedAtFrom(),
                dto.getUpdatedAtTo(),
                dto.getPageNumber(),
                dto.getPageSize(),
                dto.getSortBy(),
                dto.getSortDirection()
        );
    }

    /**
     * Convierte Map de parámetros a modelo de dominio
     * Usado cuando los parámetros vienen como query params
     */
    public static UserFilterEmpresa fromParams(String subscriptionId, Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return new UserFilterEmpresa(
                    subscriptionId,
                    null,null, null, null, null, null,
                    null, null, null, null,
                    null, null, null, null
            );
        }

        return new UserFilterEmpresa(
                subscriptionId,
                params.get("correo"),
                params.get("cuit"),
                params.get("razonSocial"),
                parseLong(params.get("userId")),
                params.get("telefono"),
                parseBoolean(params.get("isActive")),
                parseDateTime(params.get("createdAtFrom")),
                parseDateTime(params.get("createdAtTo")),
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
}