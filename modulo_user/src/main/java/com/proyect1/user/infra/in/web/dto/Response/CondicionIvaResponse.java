package com.proyect1.user.infra.in.web.dto.Response;

import com.proyect1.user.domain.model.empresa.CondicionFrenteIva;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para condiciones frente al IVA
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CondicionIvaResponse {

    private String id;
    private String condicion;
    private String tipo;
    private String descripcion;

    /**
     * Convierte el modelo de dominio a DTO de respuesta
     */
    public static CondicionIvaResponse fromDomain(CondicionFrenteIva condicion) {
        return CondicionIvaResponse.builder()
                .id(condicion.getId())
                .condicion(condicion.getCondicion() != null ? condicion.getCondicion().name() : null)
                .tipo(condicion.getTipo())
                .descripcion(condicion.getDescripcion())
                .build();
    }
}