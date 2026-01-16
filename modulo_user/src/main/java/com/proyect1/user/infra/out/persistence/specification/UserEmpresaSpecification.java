package com.proyect1.user.infra.out.persistence.specification;

import com.proyect1.user.infra.out.persistence.entity.UserEmpresaEntity;
import com.proyect1.user.domain.model.empresa.UserFilterEmpresa;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;


public class UserEmpresaSpecification {

    /**
     * Construye una Specification<UserEmpresaEntity> aplicando todos los filtros
     * contenidos en el record UserFilterEmpresa.
     */
    public static Specification<UserEmpresaEntity> withFilter(UserFilterEmpresa filter) {
        if (filter == null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        }

        SpecificationBuilder<UserEmpresaEntity> builder = SpecificationBuilder.of();

        // Filtros de texto (like)
        builder.like("cuit", filter.cuit());
        builder.like("razonSocial", filter.razonSocial());
        builder.like("telefono", filter.telefono());

        // Filtros de igualdad
        builder.equal("userId", filter.userId());
        builder.equal("isActive", filter.isActive());

        // Filtros de rango de fechas
        builder.between("createdAt", filter.createdAtFrom(), filter.createdAtTo());
        builder.between("updatedAt", filter.updatedAtFrom(), filter.updatedAtTo());

        return builder.build();
    }


}