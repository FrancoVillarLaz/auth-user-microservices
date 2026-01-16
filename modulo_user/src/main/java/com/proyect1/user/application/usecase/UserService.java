package com.proyect1.user.application.usecase;

import com.proyect1.user.application.command.standard.CreateStandardCommand;
import com.proyect1.user.application.command.standard.UpdateStandardCommand;
import com.proyect1.user.application.command.empresa.CreateEmpresaCommand;
import com.proyect1.user.application.command.empresa.UpdateEmpresaCommand;
import com.proyect1.user.application.strategy.core.UserCrudStrategy;
import com.proyect1.user.application.strategy.core.UserCrudStrategyFactory;
import com.proyect1.user.application.strategy.empresa.EmpresaSpecificStrategy;
import com.proyect1.user.application.strategy.standard.StandardSpecificStrategy;
import com.proyect1.user.domain.service.UserSubscriptionDomainService;
import com.proyect1.user.ports.in.UserCrudUseCase;
import com.proyect1.user.ports.in.UserSpecificUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserCrudUseCase, UserSpecificUseCase {

    private final UserCrudStrategyFactory strategyFactory;
    private final UserSubscriptionDomainService userSubscriptionDomainService;

    @Override
    public Map<String, Object> createUser(Object command, String subscriptionId) {
        log.info("Creando usuario para subscription={}", subscriptionId);

        // Validar tipo de comando según suscripción
        validateCommandType(command, subscriptionId);

        UserCrudStrategy strategy = strategyFactory.getStrategy(subscriptionId);
        return strategy.createUser(command, subscriptionId);
    }

    @Override
    public Map<String, Object> getUserInfo(Long userId, String subscriptionId) {
        log.debug("Obteniendo info de usuario userId={}, subscription={}", userId, subscriptionId);

        userSubscriptionDomainService.validateUserSubscription(userId, subscriptionId);
        UserCrudStrategy strategy = strategyFactory.getStrategy(subscriptionId);
        return strategy.getUserInfo(userId, subscriptionId);
    }

    @Override
    public Map<String, Object> getAllUsers(String subscriptionId, Object filter, Pageable pageable) {
        log.debug("Obteniendo todos los usuarios para subscription={}", subscriptionId);

        UserCrudStrategy strategy = strategyFactory.getStrategy(subscriptionId);
        return strategy.getAllUsers(subscriptionId, filter, pageable);
    }

    @Override
    public Map<String, Object> getAllUsersWithEmail(String subscriptionId, Object filter, Pageable pageable) {
        log.debug("Obteniendo todos los usuarios para subscription={}", subscriptionId);

        UserCrudStrategy strategy = strategyFactory.getStrategy(subscriptionId);
        return strategy.getAllUsers(subscriptionId, filter, pageable);
    }

    @Override
    public Map<String, Object> updateUser(Long userId, Object command, String subscriptionId) {
        log.info("Actualizando usuario userId={}, subscription={}", userId, subscriptionId);

        validateCommandType(command, subscriptionId);
        userSubscriptionDomainService.validateUserSubscription(userId, subscriptionId);

        UserCrudStrategy strategy = strategyFactory.getStrategy(subscriptionId);
        return strategy.updateUser(userId, command, subscriptionId);
    }

    @Override
    public Map<String, Object> deactivateUser(Long userId, String subscriptionId) {
        log.info("Desactivando usuario userId={}, subscription={}", userId, subscriptionId);

        userSubscriptionDomainService.validateUserSubscription(userId, subscriptionId);
        UserCrudStrategy strategy = strategyFactory.getStrategy(subscriptionId);
        return strategy.deactivateUser(userId, subscriptionId);
    }

    @Override
    public Map<String, Object> activateUser(Long userId, String subscriptionId) {
        log.info("Activando usuario userId={}, subscription={}", userId, subscriptionId);

        userSubscriptionDomainService.validateUserSubscription(userId, subscriptionId);
        UserCrudStrategy strategy = strategyFactory.getStrategy(subscriptionId);
        return strategy.activateUser(userId, subscriptionId);
    }

    @Override
    public Map<String, Object> getUserStandardDomicilio(Long userId, String subscriptionId) {
        log.debug("Obteniendo domicilio de usuario STANDARD userId={}", userId);

        userSubscriptionDomainService.validateUserSubscription(userId, subscriptionId);

        UserCrudStrategy strategy = strategyFactory.getStrategy(subscriptionId);

        if (strategy instanceof StandardSpecificStrategy specificStrategy) {
            return specificStrategy.getUserStandardDomicilio(userId, subscriptionId);
        }

        throw new IllegalArgumentException(
                "La estrategia para " + subscriptionId + " no soporta operaciones específicas de STANDARD"
        );
    }

    @Override
    public Map<String, Object> getEmpresaByCuit(String cuit, String subscriptionId) {
        log.debug("Buscando empresa por CUIT={}, subscription={}", cuit, subscriptionId);

        if (!"EMPRESA".equalsIgnoreCase(subscriptionId)) {
            throw new IllegalArgumentException(
                    "El método getEmpresaByCuit solo está disponible para subscriptionId=EMPRESA"
            );
        }

        UserCrudStrategy strategy = strategyFactory.getStrategy(subscriptionId);

        if (strategy instanceof EmpresaSpecificStrategy specificStrategy) {
            return specificStrategy.getEmpresaByCuit(cuit, subscriptionId);
        }
        throw new IllegalArgumentException(
                "La estrategia actual (" + strategy.getClass().getSimpleName() +
                        ") no soporta la búsqueda por CUIT. Solo disponible para EMPRESA."
        );
    }

    @Override
    public Map<String, Object> getEmpresaById(Long empresaId, String subscriptionId) {
        log.debug("Buscando empresa por ID={}, subscription={}", empresaId, subscriptionId);

        if (!"EMPRESA".equalsIgnoreCase(subscriptionId)) {
            throw new IllegalArgumentException(
                    "El método getEmpresaById solo está disponible para subscriptionId=EMPRESA"
            );
        }

        UserCrudStrategy strategy = strategyFactory.getStrategy(subscriptionId);

        if (strategy instanceof EmpresaSpecificStrategy specificStrategy) {
            return specificStrategy.getEmpresaById(empresaId, subscriptionId);
        }

        throw new IllegalArgumentException(
                "La estrategia actual (" + strategy.getClass().getSimpleName() +
                        ") no soporta la búsqueda por CUIT. Solo disponible para EMPRESA."
        );
    }

    // ===== MÉTODO DE VALIDACIÓN PRIVADO =====

    private void validateCommandType(Object command, String subscriptionId) {
        if ("STANDARD".equals(subscriptionId)) {
            if (!(command instanceof CreateStandardCommand) && !(command instanceof UpdateStandardCommand)) {
                throw new IllegalArgumentException(
                        String.format("Para suscripción STANDARD se requiere CreateUserCommand o UpdateUserCommand. Recibido: %s",
                                command != null ? command.getClass().getSimpleName() : "null")
                );
            }
        } else if ("EMPRESA".equalsIgnoreCase(subscriptionId)) {
            if (!(command instanceof CreateEmpresaCommand) && !(command instanceof UpdateEmpresaCommand)) {
                throw new IllegalArgumentException(
                        String.format("Para suscripción EMPRESA se requiere CreateEmpresaCommand o UpdateEmpresaCommand. Recibido: %s",
                                command != null ? command.getClass().getSimpleName() : "null")
                );
            }
        }
    }
}