package com.proyect1.user.infra.out.persistence.repository;

import com.proyect1.user.infra.out.persistence.entity.UserEmpresaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;
import org.springframework.stereotype.Repository;


@Repository
public interface UserEmpresaRepository extends JpaRepository<UserEmpresaEntity, Long>, JpaSpecificationExecutor<UserEmpresaEntity> {

    Optional<UserEmpresaEntity> findById(Long id);

    Optional<UserEmpresaEntity> findByAuthUserId(Long userId);

    Optional<UserEmpresaEntity> findByCuit(String cuit);

    boolean existsByCuit(String cuit);

    boolean existsByAuthUserId(Long userId);
}