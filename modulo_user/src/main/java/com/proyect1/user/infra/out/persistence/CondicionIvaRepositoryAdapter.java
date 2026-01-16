package com.proyect1.user.infra.out.persistence;

import com.proyect1.user.domain.model.empresa.CondicionFrenteIva;
import com.proyect1.user.infra.out.persistence.mapper.CondicionIvaMapper;
import com.proyect1.user.infra.out.persistence.repository.CondicionIvaRepository;
import com.proyect1.user.ports.out.CondicionIvaRepositoryPort;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
@Transactional(readOnly = true)
public class CondicionIvaRepositoryAdapter implements CondicionIvaRepositoryPort {

    CondicionIvaRepository condicionIvaRepository;
    CondicionIvaMapper mapper;



    @Override
    public Optional<CondicionFrenteIva> findById(String id) {

        log.debug("Buscando UserEmpresa por id: {}", id);
        return condicionIvaRepository.findById(id)
                .map(mapper::toDomain);
    }
}