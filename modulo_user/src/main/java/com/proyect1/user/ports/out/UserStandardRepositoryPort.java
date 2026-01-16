package com.proyect1.user.ports.out;

import com.proyect1.user.domain.model.standard.UserFilterStandard;
import com.proyect1.user.domain.model.standard.UserStandard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de Salida (OUT Port) para la persistencia de datos del usuario.
 * Define las operaciones CRUD que el Caso de Uso necesita para la suscripción 'STANDARD'.
 *
 * Implementado por el adaptador (UserStandardRepositoryAdapter).
 */
public interface UserStandardRepositoryPort {

    /**
     * Busca la información de la suscripción STANDARD para un usuario por su ID.
     *
     * @param userId El ID del usuario.
     * @return Un Optional que contiene el modelo de dominio UserStandard.
     */
    Optional<UserStandard> findUserStandardById(Long userId);

    /**
     * Verifica si existe un usuario STANDARD con el ID dado.
     *
     * @param userId El ID del usuario.
     * @return true si existe el usuario, false si no.
     */
    boolean existsUserStandard(Long userId);

    /**
     * Persiste (guarda o actualiza) un objeto UserStandard.
     *
     * @param user El modelo de dominio UserStandard a guardar.
     * @return El modelo de dominio UserStandard persistido.
     */
    UserStandard saveUserStandard(UserStandard user);

    /**
     * Busca la información de la suscripción STANDARD para un usuario por su número de documento.
     *
     * @param numeroDocumento El número de documento (DNI/CUIT, etc.).
     * @return Un Optional que contiene el modelo de dominio UserStandard.
     */
    Optional<UserStandard> findUserStandardByDocumento(String numeroDocumento);


    Page<UserStandard> findAllWithFilters(UserFilterStandard filter, Pageable pageable);


    List<UserStandard> findAllWithFilters(UserFilterStandard filter);
}