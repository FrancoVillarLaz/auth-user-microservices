package com.proyect1.user.application.usecase;

import com.proyect1.user.domain.model.empresa.CondicionFrenteIva;
import com.proyect1.user.ports.in.CondicionIvaUseCase;
import com.proyect1.user.ports.out.CondicionIvaRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CondicionIvaService implements CondicionIvaUseCase {

    private final CondicionIvaRepositoryPort condicionIvaRepositoryPort;

    @Override
    public Optional<CondicionFrenteIva> findById(String id) {
        log.debug("Buscando condición de IVA con ID: {}", id);

        if (id == null || id.trim().isEmpty()) {
            return Optional.empty();
        }

        try {
            Optional<CondicionFrenteIva> condicion = condicionIvaRepositoryPort.findById(id);

            if (condicion.isEmpty()) {
                log.warn("No se encontró condición de IVA con ID: {}", id);
            }

            return condicion;
        } catch (Exception e) {
            log.error("Error obteniendo condición de IVA con ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error al obtener condición de IVA", e);
        }
    }
}