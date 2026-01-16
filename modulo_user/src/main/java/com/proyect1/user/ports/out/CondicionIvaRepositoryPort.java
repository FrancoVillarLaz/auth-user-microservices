package com.proyect1.user.ports.out;

import com.proyect1.user.domain.model.empresa.CondicionFrenteIva;

import java.util.Optional;

public interface CondicionIvaRepositoryPort {

    Optional<CondicionFrenteIva> findById(String id);
}
