package com.proyect1.user.infra.out.persistence.specification;

import com.proyect1.user.infra.out.persistence.entity.UserStandardEntity;
import com.proyect1.user.domain.model.standard.UserFilterStandard;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class UserStandardSpecification {

    /**
     * Construye una Specification<UserStandardEntity> aplicando todos los filtros
     * contenidos en el record UserFilterStandard.
     */
    public static Specification<UserStandardEntity> withFilter(UserFilterStandard filter) {
        if (filter == null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        }

        SpecificationBuilder<UserStandardEntity> builder = SpecificationBuilder.of();

        // Filtros de texto (like)
        builder.like("nombre", filter.nombre());
        builder.like("apellido", filter.apellido());
        builder.like("manzana", filter.manzana());
        builder.like("lote", filter.lote());
        builder.like("telefono", filter.telefono());
        builder.like("numeroDocumento", filter.numeroDocumento());

        // Filtros de igualdad
        builder.equal("userId", filter.userId());
        builder.equal("establecimientoId", filter.establecimientoId());
        builder.equal("isActive", filter.isActive());

        // Filtro IN para internalType
        builder.in("internalType", filter.internalType());

        // Filtro de rango de fechas
        builder.between("createdAt", filter.createdAtStart(), filter.createdAtEnd());


        return builder.build();
    }


    /**
     * Método para búsqueda solo por userId
     */
    public static Specification<UserStandardEntity> byUserId(Long userId) {
        return (root, query, criteriaBuilder) ->
                userId == null ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get("userId"), userId);
    }

    /**
     * Método para búsqueda solo por documento
     */
    public static Specification<UserStandardEntity> byDocumento(String numeroDocumento) {
        return (root, query, criteriaBuilder) ->
                (numeroDocumento == null || numeroDocumento.trim().isEmpty()) ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get("numeroDocumento"), numeroDocumento);
    }

}