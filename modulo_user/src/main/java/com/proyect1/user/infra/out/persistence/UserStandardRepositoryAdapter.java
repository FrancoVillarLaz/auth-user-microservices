package com.proyect1.user.infra.out.persistence;


import com.proyect1.user.domain.model.standard.UserStandard;
import com.proyect1.user.infra.out.persistence.mapper.UserStandardMapper;
import com.proyect1.user.infra.out.persistence.repository.UserStandardRepository;
import com.proyect1.user.domain.model.standard.UserFilterStandard;
import com.proyect1.user.ports.out.UserStandardRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.proyect1.user.infra.out.persistence.specification.UserStandardSpecification.withFilter;

/**
 * Adapter OUT - Implementa el puerto de persistencia
 * Adapta JPA al dominio
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserStandardRepositoryAdapter implements UserStandardRepositoryPort {

    private final UserStandardRepository jpaRepository;
    private final UserStandardMapper mapper;


    @Override
    public Optional<UserStandard> findUserStandardById(Long userId) {
        return jpaRepository.findByAuthUserId(userId)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsUserStandard(Long userId) {
        return jpaRepository.existsByAuthUserId(userId);
    }


    @Override
    @Transactional
    public UserStandard saveUserStandard(UserStandard user) {
        var entity = mapper.toEntity(user);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<UserStandard> findUserStandardByDocumento(String numeroDocumento) {
        return jpaRepository.findByNumeroDocumento(numeroDocumento)
                .map(mapper::toDomain);
    }

    @Override
    public Page<UserStandard> findAllWithFilters(UserFilterStandard filter, Pageable pageable) {
        log.debug("Aplicando spec para UserStandard con filtro: {}, pageable: {}", filter, pageable);
        var spec = withFilter(filter);
        log.debug("Specification creada: {}", spec);

        return jpaRepository.findAll(spec, pageable)
                .map(mapper::toDomain);
    }




    @Override
    public List<UserStandard> findAllWithFilters(UserFilterStandard filter) {
        log.debug("Aplicando spec para UserStandard con filtro: {}", filter);
        var spec = withFilter(filter);

        return jpaRepository.findAll(spec).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}