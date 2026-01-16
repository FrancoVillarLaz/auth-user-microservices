package com.proyect1.user.infra.in.web.dto.Response;

import com.proyect1.user.domain.model.standard.UserStandard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para usuarios
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long userId;
    private String nombre;
    private String apellido;
    private String nombreCompleto;
    private String internalType;
    //private Boolean isPropietario;
    private String numeroDocumento;
    private Long establecimientoId;
    private String manzana;
    private String lote;
    private String telefono;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Convierte el modelo de dominio a DTO de respuesta
     */
    public static UserResponse fromDomain(UserStandard user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .nombre(user.getNombre())
                .apellido(user.getApellido())
                .nombreCompleto(user.getNombreCompleto())
                .internalType(user.getInternalType())
                //.isPropietario(user.isPropietario())
                .numeroDocumento(user.getNumeroDocumento())
                .establecimientoId(user.getEstablecimientoId())
                .manzana(user.getManzana())
                .lote(user.getLote())
                .telefono(user.getTelefono())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}