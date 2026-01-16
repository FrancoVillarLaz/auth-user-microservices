package com.proyect1.user.infra.in.web.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar un usuario (todos los campos opcionales)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @NotBlank(message = "La suscripcion es obligatoria")
    private String subscriptionId;

    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
    private String apellido;

    @Size(max = 50, message = "El tipo interno no puede exceder 50 caracteres")
    private String internalType;

    @Size(max = 50, message = "El número de documento no puede exceder 50 caracteres")
    private String numeroDocumento;

    private Long establecimientoId;

    @Size(max = 50, message = "La manzana no puede exceder 50 caracteres")
    private String manzana;

    @Size(max = 50, message = "El lote no puede exceder 50 caracteres")
    private String lote;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;

    private Boolean isActive;

    private String condicionIvaId;
}