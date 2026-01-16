package com.proyect1.user.domain.model.standard;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

public record UserFilterStandard(
        String subscriptionId,

        Long userId,
        String nombre,
        String apellido,
        String numeroDocumento,
        String correo,
        List<String> internalType,
        Long establecimientoId,
        String manzana,
        String lote,
        String telefono,
        Boolean isActive,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime createdAtStart,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime createdAtEnd,

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
    public boolean hasNombreFilter() {
        return nombre != null && !nombre.trim().isEmpty();
    }

    public boolean hasApellidoFilter() {
        return apellido != null && !apellido.trim().isEmpty();
    }

    public boolean hasDocumentoFilter() {
        return numeroDocumento != null && !numeroDocumento.trim().isEmpty();
    }

    public boolean hasTelefonoFilter() {
        return telefono != null && !telefono.trim().isEmpty();
    }

    public boolean hasUbicacionFilter() {
        return (manzana != null && !manzana.trim().isEmpty()) ||
                (lote != null && !lote.trim().isEmpty());
    }

    public boolean hasDateFilters() {
        return createdAtStart != null || createdAtEnd != null ||
                updatedAtFrom != null || updatedAtTo != null;
    }

    public boolean hasInternalTypeFilter() {
        return internalType != null && !internalType.isEmpty();
    }

    /**
     * Constructor compacto para validaciones básicas
     */
    public UserFilterStandard {
        if (pageSize != null && pageSize <= 0) {
            throw new IllegalArgumentException("El tamaño de página debe ser positivo.");
        }
    }

    /**
     * Factory method para crear un filtro básico por nombre y apellido
     */
    public static UserFilterStandard basicFilter(String nombre, String apellido) {
        return new UserFilterStandard(
                null, // subscriptionId
                null, // userId
                nombre,
                apellido,
                null, // numeroDocumento
                null, //correo
                null, // internalType
                null, // establecimientoId
                null, // manzana
                null, // lote
                null, // telefono
                true, // isActive
                null, // createdAtStart
                null, // createdAtEnd
                null, // updatedAtFrom
                null, // updatedAtTo
                null, // pageNumber
                null, // pageSize
                null, // sortBy
                null  // sortDirection
        );
    }

    /**
     * Factory method para crear un filtro por documento
     */
    public static UserFilterStandard byDocumento(String numeroDocumento) {
        return new UserFilterStandard(
                null,
                null,
                null,
                null,
                numeroDocumento,
                null,
                null,
                null,
                null,
                null,
                null,
                true,
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

    /**
     * Factory method para crear un filtro por userId
     */
    public static UserFilterStandard byUserId(Long userId) {
        return new UserFilterStandard(
                null,
                userId,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                true,
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