package com.proyect1.user.infra.out.persistence.repository;

import com.proyect1.user.infra.out.persistence.entity.CondicionFrenteIvaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CondicionIvaRepository extends JpaRepository<CondicionFrenteIvaEntity, String> {
    Optional<CondicionFrenteIvaEntity> findById(String id);
}
