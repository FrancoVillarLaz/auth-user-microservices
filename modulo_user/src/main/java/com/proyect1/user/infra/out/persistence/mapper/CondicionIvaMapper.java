package com.proyect1.user.infra.out.persistence.mapper;

import com.proyect1.user.infra.out.persistence.entity.CondicionFrenteIvaEntity;
import com.proyect1.user.domain.model.empresa.CondicionFrenteIva;
import com.proyect1.user.domain.model.empresa.CondicionesIva;
import org.springframework.stereotype.Component;

@Component
public class CondicionIvaMapper {

    public CondicionFrenteIva toDomain(CondicionFrenteIvaEntity entity) {
        if (entity == null) {
            return null;
        }

        return CondicionFrenteIva.builder()
                .condicion(CondicionesIva.fromString(entity.getCondicion()))
                .tipo(entity.getTipo())
                .descripcion(entity.getDescripcion())
                .build();
    }

    public CondicionFrenteIvaEntity toEntity(CondicionFrenteIva domain) {
        if (domain == null) {
            return null;
        }

        return CondicionFrenteIvaEntity.builder()
                .condicion(domain.getCondicion() != null ? domain.getCondicion().name() : null)
                .tipo(domain.getTipo())
                .descripcion(domain.getDescripcion())
                .build();
    }
}