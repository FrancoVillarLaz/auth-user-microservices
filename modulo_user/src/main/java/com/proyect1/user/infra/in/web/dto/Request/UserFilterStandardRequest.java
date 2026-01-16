package com.proyect1.user.infra.in.web.dto.Request;

import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

public record UserFilterStandardRequest(
        String subscriptionId,

        String correo,

        Long userId,

        @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,100}$",
                message = "Nombre debe contener solo letras y tener entre 2 y 100 caracteres")
        String nombre,

        @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,100}$",
                message = "Apellido debe contener solo letras y tener entre 2 y 100 caracteres")
        String apellido,

        @Pattern(regexp = "^\\d{7,12}$",
                message = "Número de documento debe tener entre 7 y 12 dígitos")
        String numeroDocumento,

        List<String> internalType,

        Long establecimientoId,

        @Pattern(regexp = "^[A-Z0-9]{1,10}$",
                message = "Manzana debe ser alfanumérico (max 10 caracteres)")
        String manzana,

        @Pattern(regexp = "^[A-Z0-9]{1,10}$",
                message = "Lote debe ser alfanumérico (max 10 caracteres)")
        String lote,

        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$",
                message = "Teléfono inválido")
        String telefono,

        Boolean isActive,

        // Filtros de fecha
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime createdAtStart,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime createdAtEnd,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime updatedAtFrom,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime updatedAtTo,

        // Paginación (opcional, ya que también viene en Pageable)
        @jakarta.validation.constraints.Min(value = 0, message = "pageNumber debe ser >= 0")
        Integer pageNumber,

        @jakarta.validation.constraints.Min(value = 1, message = "pageSize debe ser >= 1")
        @jakarta.validation.constraints.Max(value = 100, message = "pageSize debe ser <= 100")
        Integer pageSize,

        String sortBy,

        @Pattern(regexp = "^(asc|desc)$",
                message = "sortDirection debe ser 'asc' o 'desc'")
        String sortDirection
) {

    /**
     * Constructor compacto con validaciones adicionales
     */
    public UserFilterStandardRequest {
        if (pageSize != null && pageSize > 100) {
            pageSize = 100; // Limitar tamaño máximo de página
        }
    }

    /**
     * Método para verificar si hay algún filtro activo
     */
    public boolean hasAnyFilter() {
        return userId != null ||
                (nombre != null && !nombre.isBlank()) ||
                (apellido != null && !apellido.isBlank()) ||
                (numeroDocumento != null && !numeroDocumento.isBlank()) ||
                (internalType != null && !internalType.isEmpty()) ||
                establecimientoId != null ||
                (manzana != null && !manzana.isBlank()) ||
                (lote != null && !lote.isBlank()) ||
                (telefono != null && !telefono.isBlank()) ||
                isActive != null ||
                createdAtStart != null ||
                createdAtEnd != null ||
                updatedAtFrom != null ||
                updatedAtTo != null;
    }
}