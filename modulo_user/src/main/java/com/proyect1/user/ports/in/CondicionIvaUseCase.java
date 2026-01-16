package com.proyect1.user.ports.in;

import com.proyect1.user.domain.model.empresa.CondicionFrenteIva;

import java.util.Optional;

public interface CondicionIvaUseCase {
    Optional<CondicionFrenteIva> findById(String id);
}
