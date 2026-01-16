package com.proyect1.user.ports.out;

import com.proyect1.user.domain.model.empresa.UserEmpresa;
import com.proyect1.user.domain.model.empresa.UserFilterEmpresa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de Salida (OUT Port) para la persistencia de datos del usuario.
 * Define las operaciones CRUD que el Caso de Uso necesita para la suscripción 'INNCOME'.
 *
 * Implementado por el adaptador (UserStandardRepositoryAdapter).
 */
public interface UserEmpresaRepositoryPort {

        /**
         * Busca la información de la suscripción INNCOME para un usuario por su ID.
         *
         * @param userId El ID del usuario.
         * @return Un Optional que contiene el modelo de dominio UserStandard.
         */
        Optional<UserEmpresa> findUserEmpresaById(Long userId);

        Optional<UserEmpresa> findUserEmpresaByUserId(Long userId);

        Optional<UserEmpresa> findUserEmpresaByCuit(String cuit);

        UserEmpresa saveUserEmpresa(UserEmpresa userEmpresa);

        void deleteUserEmpresa(Long id);

        Page<UserEmpresa> findAllWithFilters(UserFilterEmpresa filter, Pageable pageable);

        List<UserEmpresa> findAllWithFilters(UserFilterEmpresa filter);

        boolean existsByCuit(String cuit);

        boolean existsByUserId(Long userId);
}
