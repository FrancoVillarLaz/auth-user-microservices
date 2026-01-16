package com.proyect1.user.domain.model.empresa;

import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public record UserFilterEmpresa(
        String subscriptionId,

        String correo,
        String cuit,
        String razonSocial,
        Long userId,
        String telefono,
        Boolean isActive,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime createdAtFrom,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime createdAtTo,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime updatedAtFrom,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime updatedAtTo,

        // Campos de paginación (opcionales)
        Integer pageNumber,
        Integer pageSize,
        String sortBy,
        String sortDirection
) {

    /**
     * Métodos de utilidad para verificar si hay filtros
     */
    public boolean hasCuitFilter() {
        return cuit != null && !cuit.trim().isEmpty();
    }

    public boolean hasRazonSocialFilter() {
        return razonSocial != null && !razonSocial.trim().isEmpty();
    }

    public boolean hasTelefonoFilter() {
        return telefono != null && !telefono.trim().isEmpty();
    }

    public boolean hasDateFilters() {
        return createdAtFrom != null || createdAtTo != null ||
                updatedAtFrom != null || updatedAtTo != null;
    }

    /**
     * Constructor compacto para validaciones básicas
     */
    public UserFilterEmpresa {
        if (pageSize != null && pageSize <= 0) {
            throw new IllegalArgumentException("El tamaño de página debe ser positivo.");
        }
    }

    /**
     * Factory method para crear un filtro básico
     */
    public static UserFilterEmpresa basicFilter(String cuit, String razonSocial) {
        return new UserFilterEmpresa(
                null, // subscriptionId
                null, // correo
                cuit,
                razonSocial,
                null, // userId
                null, // telefono
                true, // isActive
                null, // createdAtFrom
                null, // createdAtTo
                null, // updatedAtFrom
                null, // updatedAtTo
                null, // pageNumber
                null, // pageSize
                null, // sortBy
                null  // sortDirection
        );
    }
}