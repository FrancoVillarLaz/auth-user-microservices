package com.proyect1.user.infra.out.persistence;

import com.proyect1.user.infra.out.persistence.entity.UserEmpresaEntity;
import com.proyect1.user.infra.out.persistence.mapper.UserEmpresaMapper;
import com.proyect1.user.infra.out.persistence.repository.UserEmpresaRepository;
import com.proyect1.user.domain.model.empresa.UserEmpresa;
import com.proyect1.user.domain.model.empresa.UserFilterEmpresa;
import com.proyect1.user.ports.out.UserEmpresaRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import com.proyect1.user.infra.out.persistence.specification.UserEmpresaSpecification;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserEmpresaRepositoryAdapter implements UserEmpresaRepositoryPort {


    private final UserEmpresaRepository userEmpresaRepository;
    private final UserEmpresaMapper userEmpresaMapper;

    @Override
    public Optional<UserEmpresa> findUserEmpresaById(Long id) {
        log.debug("Buscando UserEmpresa por id: {}", id);
        return userEmpresaRepository.findById(id)
                .map(userEmpresaMapper::toDomain);
    }

    @Override
    public Optional<UserEmpresa> findUserEmpresaByUserId(Long userId) {
        log.debug("Buscando UserEmpresa por userId: {}", userId);
        return userEmpresaRepository.findByAuthUserId(userId)
                .map(userEmpresaMapper::toDomain);
    }

    @Override
    public Optional<UserEmpresa> findUserEmpresaByCuit(String cuit) {
        log.debug("Buscando UserEmpresa por cuit: {}", cuit);
        return userEmpresaRepository.findByCuit(cuit)
                .map(userEmpresaMapper::toDomain);
    }

    @Override
    @Transactional
    public UserEmpresa saveUserEmpresa(UserEmpresa userEmpresa) {
        log.debug("mapendo UserEmpresa domain a enttiy: {}", userEmpresa);
        UserEmpresaEntity entity = userEmpresaMapper.toEntity(userEmpresa);
        log.debug("guardando el userEmpresa entity: {}", entity);
        UserEmpresaEntity savedEntity = userEmpresaRepository.save(entity);
        return userEmpresaMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteUserEmpresa(Long id) {
        log.debug("Eliminando UserEmpresa id: {}", id);
        userEmpresaRepository.deleteById(id);
    }

    @Override
    public Page<UserEmpresa> findAllWithFilters(UserFilterEmpresa filter, Pageable pageable) {
        log.debug("Buscando UserEmpresas con filtros: {}, paginación: {}", filter, pageable);
        var spec = UserEmpresaSpecification.withFilter(filter);
        return userEmpresaRepository.findAll(spec, pageable)
                .map(userEmpresaMapper::toDomain);
    }

    @Override
    public List<UserEmpresa> findAllWithFilters(UserFilterEmpresa filter) {
        log.debug("Buscando UserEmpresas con filtros: {}", filter);
        var spec = UserEmpresaSpecification.withFilter(filter);
        return userEmpresaRepository.findAll(spec)
                .stream()
                .map(userEmpresaMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByCuit(String cuit) {
        return userEmpresaRepository.existsByCuit(cuit);
    }

    @Override
    public boolean existsByUserId(Long userId) {
        return userEmpresaRepository.existsByAuthUserId(userId);
    }
}