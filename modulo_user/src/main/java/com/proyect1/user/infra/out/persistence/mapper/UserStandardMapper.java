package com.proyect1.user.infra.out.persistence.mapper;

import com.proyect1.user.domain.model.standard.UserStandard;
import com.proyect1.user.infra.out.persistence.entity.UserStandardEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper entre modelo de dominio y entidad JPA
 * Mantiene el dominio libre de anotaciones JPA
 */
@Component
public class UserStandardMapper {

    /**
     * Convierte entidad JPA a modelo de dominio
     */
    public UserStandard toDomain(UserStandardEntity entity) {
        if (entity == null) {
            return null;
        }

        // Mapear correo desde AuthUser si está disponible
        String correo = null;


        return UserStandard.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .apellido(entity.getApellido())
                .correo(correo)
                .internalType(entity.getInternalType())
                .numeroDocumento(entity.getNumeroDocumento())
                .establecimientoId(entity.getEstablecimientoId())
                .manzana(entity.getManzana())
                .lote(entity.getLote())
                .telefono(entity.getTelefono())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Convierte modelo de dominio a entidad JPA
     */
    public UserStandardEntity toEntity(UserStandard domain) {
        if (domain == null) {
            return null;
        }

        return UserStandardEntity.builder()
                .id(domain.getId())
                .authUserId(domain.getUserId())
                .nombre(domain.getNombre())
                .apellido(domain.getApellido())
                .internalType(domain.getInternalType())
                .numeroDocumento(domain.getNumeroDocumento())
                .establecimientoId(domain.getEstablecimientoId())
                .manzana(domain.getManzana())
                .lote(domain.getLote())
                .telefono(domain.getTelefono())
                .isActive(domain.getIsActive())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    /**
     * Actualiza una entidad existente con datos del dominio
     */
    public void updateEntity(UserStandardEntity entity, UserStandard domain) {
        if (entity == null || domain == null) {
            return;
        }

        entity.setNombre(domain.getNombre());
        entity.setApellido(domain.getApellido());
        entity.setInternalType(domain.getInternalType());
        entity.setNumeroDocumento(domain.getNumeroDocumento());
        entity.setEstablecimientoId(domain.getEstablecimientoId());
        entity.setManzana(domain.getManzana());
        entity.setLote(domain.getLote());
        entity.setTelefono(domain.getTelefono());
        entity.setIsActive(domain.getIsActive());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setAuthUserId(domain.getUserId());

    }
}