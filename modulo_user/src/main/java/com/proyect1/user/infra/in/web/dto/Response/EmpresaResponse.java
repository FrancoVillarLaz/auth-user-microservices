package com.proyect1.user.infra.in.web.dto.Response;

import com.proyect1.user.domain.model.empresa.UserEmpresa;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para empresas
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaResponse {

    private Long id;
    private String cuit;
    private String razonSocial;
    private String telefono;
    private String internalType;
    private String condicionIvaId;
    private String condicionIvaDescripcion;
    private Long userId;
    private String correo;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Convierte el modelo de dominio a DTO de respuesta
     */
    public static EmpresaResponse fromDomain(UserEmpresa empresa) {
        return EmpresaResponse.builder()
                .id(empresa.getId())
                .cuit(empresa.getCuit())
                .razonSocial(empresa.getRazonSocial())
                .telefono(empresa.getTelefono())
                .internalType(empresa.getInternalType())
                .condicionIvaId(empresa.getCondicionFrenteIva() != null ?
                        empresa.getCondicionFrenteIva().getId() : null)
                .condicionIvaDescripcion(empresa.getCondicionFrenteIva() != null ?
                        empresa.getCondicionFrenteIva().getDescripcion() : null)
                .userId(empresa.getUserAuthId())
                .correo(empresa.getCorreo())
                .isActive(empresa.getIsActive())
                .createdAt(empresa.getCreatedAt())
                .updatedAt(empresa.getUpdatedAt())
                .build();
    }
}