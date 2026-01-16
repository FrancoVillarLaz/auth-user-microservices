package com.proyect1.user.application.usecase;

import com.proyect1.user.application.command.CreateUserFromAuthCommand;
import com.proyect1.user.application.kafka.input.AuthEvent;
import com.proyect1.user.ports.in.CreateUserFromAuthUseCase;
import com.proyect1.user.ports.out.CachePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthEventService {

    private final CachePort cache;
    private final CreateUserFromAuthUseCase createUserFromAuthUseCase;


    /**
     * Procesa el evento según su tipo
     */
    public void handle(AuthEvent event) {
        switch (event.type()) {
            case USER_REGISTERED -> handleUserRegistered(event);
            case USER_LOGGED_IN -> handleUserLoggedIn(event);
            case USER_LOGGED_OUT -> handleUserLoggedOut(event);
            case TOKEN_REFRESHED -> handleTokenRefreshed(event);
            case LOGIN_FAILED -> handleLoginFailed(event);
            case REGISTRATION_FAILED -> handleRegistrationFailed(event);
        }
    }

    private void handleUserRegistered(AuthEvent event) {
        CreateUserFromAuthCommand command =
                CreateUserFromAuthCommand.from(event);
        log.debug("createuserfromauthcommand: {}", command);

        createUserFromAuthUseCase.execute(command);
    }


    private void handleUserLoggedIn(AuthEvent event) {
        log.info("User logged in: {} desde IP: {}", event.username(), event.ipAddress());
        // Invalidar cache para que se obtengan datos frescos
        invalidateUserCache(event.userId());
    }

    private void handleUserLoggedOut(AuthEvent event) {
        log.info("User logged out: {}", event.username());
        // Invalidar todo el cache del usuario
        invalidateUserCache(event.userId());
    }

    private void handleTokenRefreshed(AuthEvent event) {
        log.debug("Token refrescado para user {}", event.userId());
        // Opcional: actualizar timestamp de actividad
    }

    private void handleLoginFailed(AuthEvent event) {
        log.warn("login fallo para: {} de direccion IP: {} - motivo/razon: {}",
                event.username(), event.ipAddress(), event.metaData());
        // Opcional: registrar intento fallido para detección de ataques
    }

    private void handleRegistrationFailed(AuthEvent event) {
        log.warn("Registration failed: {} - {}", event.username(), event.metaData());
    }

    /**
     * Invalida el cache de un usuario
     */
    private void invalidateUserCache(Long userId) {
        try {
            String pattern = "user:info:" + userId + "*";
            cache.invalidatePattern(pattern);
            log.debug("cache invalida para userId={}", userId);
        } catch (Exception e) {
            log.error("Error invalidando cache para userId={}: {}", userId, e.getMessage());
        }
    }
}

