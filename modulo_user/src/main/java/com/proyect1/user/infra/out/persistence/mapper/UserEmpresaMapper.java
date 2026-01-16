package com.proyect1.user.infra.out.persistence.mapper;

import com.proyect1.user.domain.model.empresa.CondicionesIva;
import com.proyect1.user.infra.out.persistence.entity.UserEmpresaEntity;
import com.proyect1.user.infra.out.persistence.entity.CondicionFrenteIvaEntity;
import com.proyect1.user.domain.model.empresa.UserEmpresa;
import com.proyect1.user.domain.model.empresa.CondicionFrenteIva;
import org.springframework.stereotype.Component;

/**
 * Mapper entre modelo de dominio UserEmpresa y entidad JPA UserEmpresaEntity
 * Mantiene el dominio libre de anotaciones JPA
 */
@Component
public class UserEmpresaMapper {

    /**
     * Convierte entidad JPA a modelo de dominio
     */
    public UserEmpresa toDomain(UserEmpresaEntity entity) {
        if (entity == null) {
            return null;
        }

        // Mapear CondicionFrenteIva
        CondicionFrenteIva condicionFrenteIva = null;
        if (entity.getCondicionFrenteIva() != null) {
            condicionFrenteIva = CondicionFrenteIva.builder()
                    .id(entity.getCondicionFrenteIva().getId().toString())
                    .condicion(CondicionesIva
                            .fromString(entity.getCondicionFrenteIva().getCondicion()))
                    .tipo(entity.getCondicionFrenteIva().getTipo())
                    .descripcion(entity.getCondicionFrenteIva().getDescripcion())
                    .build();
        }

        // Obtener correo desde AuthUser si existe
        String correo = null;


        return UserEmpresa.builder()
                .id(entity.getId())
                .cuit(entity.getCuit())
                .razonSocial(entity.getRazonSocial())
                .telefono(entity.getTelefono())
                .userAuthId(entity.getAuthUserId())
                .internalType(entity.getInternalType())
                .correo(correo)
                .CondicionFrenteIva(condicionFrenteIva)
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Convierte modelo de dominio a entidad JPA
     */
    public UserEmpresaEntity toEntity(UserEmpresa domain) {
        if (domain == null) {
            return null;
        }

        // Crear CondicionFrenteIvaEntity si existe
        CondicionFrenteIvaEntity condicionEntity = null;
        if (domain.getCondicionFrenteIva() != null) {
            condicionEntity = CondicionFrenteIvaEntity.builder()
                    .id(Long.parseLong(domain.getCondicionFrenteIva().getId()))
                    .condicion(domain.getCondicionFrenteIva().getCondicion() != null
                            ? domain.getCondicionFrenteIva().getCondicion().name()
                            : null)
                    .tipo(domain.getCondicionFrenteIva().getTipo())
                    .descripcion(domain.getCondicionFrenteIva().getDescripcion())
                    .build();
        }

        return UserEmpresaEntity.builder()
                .id(domain.getId())
                .cuit(domain.getCuit())
                .razonSocial(domain.getRazonSocial())
                .telefono(domain.getTelefono())
                .internalType(domain.getInternalType())
                .authUserId(domain.getUserAuthId())
                .condicionFrenteIva(condicionEntity)
                .isActive(domain.getIsActive())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    /**
     * Actualiza una entidad existente con datos del dominio
     */
    public void updateEntity(UserEmpresaEntity entity, UserEmpresa domain) {
        if (entity == null || domain == null) {
            return;
        }

        entity.setCuit(domain.getCuit());
        entity.setRazonSocial(domain.getRazonSocial());
        entity.setTelefono(domain.getTelefono());
        entity.setInternalType(domain.getInternalType());
        entity.setIsActive(domain.getIsActive());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setAuthUserId(domain.getUserAuthId());


        // Actualizar relación con CondicionFrenteIva si es necesario
        if (domain.getCondicionFrenteIva() != null) {
            CondicionFrenteIvaEntity condicionEntity = CondicionFrenteIvaEntity.builder()
                    .id(Long.parseLong(domain.getCondicionFrenteIva().getId()))
                    .condicion(domain.getCondicionFrenteIva().getCondicion() != null
                            ? domain.getCondicionFrenteIva().getCondicion().name()
                            : null)
                    .tipo(domain.getCondicionFrenteIva().getTipo())
                    .descripcion(domain.getCondicionFrenteIva().getDescripcion())
                    .build();
            entity.setCondicionFrenteIva(condicionEntity);
        } else {
            entity.setCondicionFrenteIva(null);
        }
    }
}