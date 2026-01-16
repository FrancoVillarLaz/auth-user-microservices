package com.proyect1.user.infra.out.persistence.repository;

import com.proyect1.user.infra.out.persistence.entity.UserStandardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository JPA - Spring Data genera la implementación
 */
@Repository
public interface UserStandardRepository extends JpaRepository<UserStandardEntity, Long>, JpaSpecificationExecutor<UserStandardEntity> {

    Optional<UserStandardEntity> findByAuthUserId(Long userId);

    Optional<UserStandardEntity> findByNumeroDocumento(String numeroDocumento);

    boolean existsByAuthUserId(Long userId);



}