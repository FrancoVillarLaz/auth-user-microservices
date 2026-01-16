package com.proyect1.user.domain.service;

import com.proyect1.user.domain.model.standard.UserStandard;
import com.proyect1.user.ports.out.UserStandardRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;


@RequiredArgsConstructor
public class UserDomainService {

    private final UserStandardRepositoryPort userStandardRepositoryPort;

    public void validateUniqueDocument(String numeroDocumento) {
        if (numeroDocumento != null &&
                userStandardRepositoryPort.findUserStandardByDocumento(numeroDocumento).isPresent()) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario con el documento: " + numeroDocumento
            );
        }
    }

    public void validateUniqueUserAuthId(Long userId) {
        if (userStandardRepositoryPort.existsUserStandard(userId) && userId != null) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario con el ID de autenticación: " + userId
            );
        }
    }

    public void validateStadoUserSuscription(Long userId) {
        UserStandard user = userStandardRepositoryPort.findUserStandardById(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el usuario con ID: " + userId
                ));

        if (user.getIsActive() != null && !user.getIsActive()) {
            throw new IllegalStateException(
                    "El usuario con ID " + userId + " está inactivo."
            );
        }
    }

    public UserStandard initializeUser(UserStandard user) {
        if (user.getIsActive() == null) {
            user.setIsActive(true);
        }
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }
}
