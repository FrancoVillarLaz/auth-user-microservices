package com.proyect1.user.application.usecase;

import com.proyect1.user.application.command.CreateUserFromAuthCommand;
import com.proyect1.user.application.command.standard.CreateStandardCommand;
import com.proyect1.user.application.command.empresa.CreateEmpresaCommand;
import com.proyect1.user.application.kafka.events.UserCreatedEvent;
import com.proyect1.user.application.kafka.events.UserCreationFailedEvent;
import com.proyect1.user.domain.model.standard.UserStandard;
import com.proyect1.user.domain.model.empresa.UserEmpresa;
import com.proyect1.user.ports.out.CachePort;
import com.proyect1.user.ports.out.UserEventPublisherPort;
import com.proyect1.user.ports.in.CreateUserFromAuthUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateUserFromAuthService implements CreateUserFromAuthUseCase {

    private final UserService userService;
    private final UserEventPublisherPort eventPublisher;
    private final CachePort cache;

    @Override
    public void execute(CreateUserFromAuthCommand command) {
        log.info("Procesando creación de usuario desde auth: {}", command);

        try {
            Map<String, Object> result;

            if (command.isStandard()) {
                // Crear usuario STANDARD
                CreateStandardCommand createStandardCommand = toCreateStandardCommand(command);
                result = userService.createUser(createStandardCommand, "STANDARD");
                UserStandard domainObject = extractStandardFromResult(result);

                eventPublisher.publishUserCreated(
                        UserCreatedEvent.from(domainObject, command)
                );

            } else if (command.isEmpresa()) {

                log.debug("error en condicion iva: {}", command.condicionIvaId());
                // Crear empresa
                CreateEmpresaCommand createEmpresaCommand = toCreateEmpresaCommand(command);

                log.debug("Creando empresa con comando 17: {}", createEmpresaCommand);
                result = userService.createUser(createEmpresaCommand, "EMPRESA");
                UserEmpresa domainObject = extractEmpresaFromResult(result);

                eventPublisher.publishUserCreated(
                        UserCreatedEvent.from(domainObject, command)
                );

            } else {
                throw new IllegalArgumentException("Tipo de usuario no soportado: " + command.subscriptionId());
            }

            // Invalidar cache
            invalidateCache(command.userId());

            log.info("{} creado exitosamente desde auth: userId={}",
                    command.subscriptionId(), command.userId());

        } catch (Exception ex) {
            log.error("Error al crear {} desde auth: userId={}, error={}",
                    command.subscriptionId(), command.userId(), ex.getMessage(), ex);

            // Publicar evento de fallo
            eventPublisher.publishUserCreationFailed(
                    UserCreationFailedEvent.from(command, ex)
            );

            throw ex;
        }
    }

    /**
     * Convierte CreateUserFromAuthCommand a CreateStandardCommand
     */
    private CreateStandardCommand toCreateStandardCommand(CreateUserFromAuthCommand command) {
        return new CreateStandardCommand(
                command.userId(),
                command.nombre(),
                command.apellido(),
                command.internalType(),
                command.numeroDocumento(),
                command.establecimientoId(),
                command.manzana(),
                command.lote(),
                command.telefono(),
                command.email(),
                command.isActive()
        );
    }

    /**
     * Convierte CreateUserFromAuthCommand a CreateEmpresaCommand
     */
    private CreateEmpresaCommand toCreateEmpresaCommand(CreateUserFromAuthCommand command) {
        return new CreateEmpresaCommand(
                command.userId(),
                command.internalType(),
                command.razonSocial(),
                command.cuit(),
                command.telefono(),
                command.condicionIvaId(),
                command.isActive()
        );
    }

    /**
     * Extrae UserStandard del resultado de UserService
     */
    private UserStandard extractStandardFromResult(Map<String, Object> result) {

        if (result instanceof Map) {
            return convertMapToUserStandard((Map<String, Object>) result);
        }

        throw new IllegalStateException("Tipo de usuario STANDARD no soportado en el resultado");
    }

    /**
     * Extrae UserEmpresa del resultado de UserService
     */
    private UserEmpresa extractEmpresaFromResult(Map<String, Object> result) {
        // Si es un Map, convertirlo a UserEmpresa
        if (result instanceof Map) {
            return convertMapToUserEmpresa((Map<String, Object>) result);
        }

        throw new IllegalStateException("Tipo de empresa no soportado en el resultado");
    }

    private UserStandard convertMapToUserStandard(Map<String, Object> userMap) {
        return UserStandard.builder()
                .id((Long) userMap.get("id"))
                .userId((Long) userMap.get("userId"))
                .nombre((String) userMap.get("nombre"))
                .apellido((String) userMap.get("apellido"))
                .correo((String) userMap.get("correo"))
                .internalType((String) userMap.get("internalType"))
                .numeroDocumento((String) userMap.get("numeroDocumento"))
                .establecimientoId((Long) userMap.get("establecimientoId"))
                .manzana((String) userMap.get("manzana"))
                .lote((String) userMap.get("lote"))
                .telefono((String) userMap.get("telefono"))
                .isActive((Boolean) userMap.get("isActive"))
                .build();
    }

    private UserEmpresa convertMapToUserEmpresa(Map<String, Object> empresaMap) {
        // Aquí necesitarías crear el builder con todos los campos necesarios
        // Esto es un ejemplo básico
        return UserEmpresa.builder()
                .id((Long) empresaMap.get("id"))
                .cuit((String) empresaMap.get("cuit"))
                .razonSocial((String) empresaMap.get("razonSocial"))
                .telefono((String) empresaMap.get("telefono"))
                .isActive((Boolean) empresaMap.get("isActive"))
                .build();
    }

    private void invalidateCache(Long userId) {
        cache.invalidatePattern("user:info:" + userId + "*");
        log.debug("Cache invalidado para userId={}", userId);
    }
}