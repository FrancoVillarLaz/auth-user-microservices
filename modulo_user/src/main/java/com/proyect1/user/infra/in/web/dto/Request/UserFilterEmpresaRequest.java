package com.proyect1.user.infra.in.web.dto.Request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFilterEmpresaRequest {

    private String subscriptionId;

    private String correo;

    @Pattern(regexp = "\\d{11}", message = "CUIT debe tener 11 dígitos")
    private String cuit;

    private String razonSocial;

    private Long userId;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Teléfono inválido")
    private String telefono;

    private Boolean isActive;

    // Filtros de fecha
    private LocalDateTime createdAtFrom;
    private LocalDateTime createdAtTo;
    private LocalDateTime updatedAtFrom;
    private LocalDateTime updatedAtTo;

    // Paginación (opcional, ya que también viene en Pageable)
    private Integer pageNumber;
    private Integer pageSize;
    private String sortBy;
    private String sortDirection;
}