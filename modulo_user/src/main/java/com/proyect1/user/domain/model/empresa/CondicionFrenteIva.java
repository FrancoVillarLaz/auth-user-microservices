package com.proyect1.user.domain.model.empresa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CondicionFrenteIva {

    private String id;
    private CondicionesIva condicion;
    private String descripcion;
    private String tipo;

}
