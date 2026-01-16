package com.proyect1.user.application.strategy.standard;

import com.proyect1.user.application.command.standard.CreateStandardCommand;
import com.proyect1.user.application.command.standard.UpdateStandardCommand;
import com.proyect1.user.application.strategy.core.UserCrudStrategy;
import com.proyect1.user.domain.model.standard.UserFilterStandard;
import com.proyect1.user.domain.model.standard.UserStandard;
import com.proyect1.user.domain.service.UserDomainService;
import com.proyect1.user.ports.out.CachePort;
import com.proyect1.user.ports.out.UserStandardRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.proyect1.user.domain.model.SubscriptionType.isValidSub;

@Slf4j
@Component
@RequiredArgsConstructor
public class StandardStrategy implements UserCrudStrategy, StandardSpecificStrategy {

    private final UserStandardRepositoryPort repositoryPort;
    private final CachePort cachePort;
    private final UserDomainService userDomainService;

    private static final String CACHE_KEY_PREFIX = "user:";


    @Override
    public Map<String, Object> createUser(Object command, String subscriptionId) {
        if (!(command instanceof CreateStandardCommand createCommand)) {
            throw new IllegalArgumentException(
                    "Para suscripción STANDARD se requiere CreateUserCommand. Recibido: " +
                            (command != null ? command.getClass().getSimpleName() : "null")
            );
        }

        log.info("Creando usuario STANDARD con documento: {}", createCommand.numeroDocumento());

        userDomainService.validateUniqueDocument(createCommand.numeroDocumento());
        userDomainService.validateUniqueUserAuthId(createCommand.userId());
        UserStandard user = createCommand.toDomain();
        userDomainService.initializeUser(user);

        UserStandard savedUser = repositoryPort.saveUserStandard(user);
        invalidateUserCache(savedUser.getUserId());

        log.info("Usuario STANDARD creado exitosamente con ID: {}", savedUser.getUserId());
        return savedUser.toMap();
    }

    @Override
    public Map<String, Object> getUserInfo(Long userId, String subscriptionId) {
        log.debug("Obteniendo info de usuario STANDARD userId={}", userId);

        final String cacheKey = CACHE_KEY_PREFIX + userId + ":" + subscriptionId;

        return cachePort.get(cacheKey)
                .map(cached -> {
                    log.debug("Cache HIT para userId={}", userId);
                    return cached;
                })
                .orElseGet(() -> {
                    log.debug("Cache MISS para userId={}. Consultando repositorio...", userId);

                    return repositoryPort.findUserStandardById(userId)
                            .map(user -> {
                                Map<String, Object> data = user.toMap();
                                cachePort.put(cacheKey, data, 300);
                                return data;
                            })
                            .orElse(Map.of());
                });
    }

    @Override
    public Map<String, Object> getAllUsers(String subscriptionId, Object filter, Pageable pageable) {
        log.debug("Obteniendo todos los usuarios STANDARD para subscription={}", subscriptionId);

        Map<String, Object> result = new HashMap<>();
        UserFilterStandard standardFilter = convertToStandardFilter(subscriptionId, filter);

        if (pageable != null) {
            Page<UserStandard> usersPage = repositoryPort.findAllWithFilters(standardFilter, pageable);
            result.put("content", usersPage.getContent().stream()
                    .map(UserStandard::toMap)
                    .collect(Collectors.toList()));
            result.put("pagination", buildPaginationInfo(usersPage));
        } else {
            List<UserStandard> users = repositoryPort.findAllWithFilters(standardFilter);
            List<Map<String, Object>> userData = users.stream()
                    .map(UserStandard::toMap)
                    .collect(Collectors.toList());
            result.put("content", userData);
            result.put("total", userData.size());
        }

        return result;
    }

    @Override
    public Map<String, Object> getAllUsersEmail(String subscriptionId, Object filter, Pageable pageable){
        log.debug("Obteniendo todos los usuarios STANDARD con email");

        Map<String, Object> result = new HashMap<>();
        UserFilterStandard standardFilter = convertToStandardFilter(subscriptionId, filter);


        List<UserStandard> users = repositoryPort.findAllWithFilters(standardFilter);
        List<Map<String, Object>> userData = users.stream()
                .map(UserStandard::toMap)
                .collect(Collectors.toList());
        result.put("content", userData);
        result.put("total", userData.size());

        return result;

    }

    @Override
    public Map<String, Object> updateUser(Long userId, Object command, String subscriptionId) {
        if (!(command instanceof UpdateStandardCommand updateCommand)) {
            throw new IllegalArgumentException(
                    "Para suscripción STANDARD se requiere UpdateUserCommand. Recibido: " +
                            (command != null ? command.getClass().getSimpleName() : "null")
            );
        }

        log.info("Actualizando usuario STANDARD ID: {}", userId);

        UserStandard existingUser = repositoryPort.findUserStandardById(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el usuario STANDARD con ID: " + userId
                ));

        // Validar documento único si se está cambiando
        if (updateCommand.numeroDocumento() != null &&
                !updateCommand.numeroDocumento().equals(existingUser.getNumeroDocumento())) {

            repositoryPort.findUserStandardByDocumento(updateCommand.numeroDocumento())
                    .ifPresent(u -> {
                        throw new IllegalArgumentException(
                                "Ya existe otro usuario con el documento: " + updateCommand.numeroDocumento()
                        );
                    });
        }

        updateUserFields(existingUser, updateCommand);
        existingUser.setUpdatedAt(LocalDateTime.now());

        UserStandard updatedUser = repositoryPort.saveUserStandard(existingUser);
        log.info("Usuario STANDARD actualizado exitosamente: {}", userId);
        invalidateUserCache(userId);

        return updatedUser.toMap();
    }

    @Override
    public Map<String, Object> deactivateUser(Long userId, String subscriptionId) {
        log.info("Desactivando usuario STANDARD ID: {}", userId);

        UserStandard user = repositoryPort.findUserStandardById(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el usuario STANDARD con ID: " + userId
                ));

        user.deactivate();
        repositoryPort.saveUserStandard(user);
        log.info("Usuario STANDARD desactivado exitosamente: {}", userId);
        invalidateUserCache(userId);

        return Map.of(
                "userId", userId,
                "status", "DEACTIVATED",
                "message", "Usuario desactivado exitosamente",
                "timestamp", LocalDateTime.now()
        );
    }

    @Override
    public Map<String, Object> activateUser(Long userId, String subscriptionId) {
        log.info("Activando usuario STANDARD ID: {}", userId);

        UserStandard user = repositoryPort.findUserStandardById(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el usuario STANDARD con ID: " + userId
                ));

        user.activate();
        repositoryPort.saveUserStandard(user);
        log.info("Usuario STANDARD activado exitosamente: {}", userId);
        invalidateUserCache(userId);

        return Map.of(
                "userId", userId,
                "status", "ACTIVATED",
                "message", "Usuario activado exitosamente",
                "timestamp", LocalDateTime.now()
        );
    }

    @Override
    public String getSupportedSubscriptionId(String sub) {
        boolean isValid = isValidSub(sub);
        log.trace("Validando subscriptionId={} para STANDARD: {}", sub, isValid);
        return isValid ? sub : null;
    }

    // ===== IMPLEMENTACIÓN DE StandardSpecificStrategy =====

    @Override
    public Map<String, Object> getUserStandardDomicilio(Long userId, String subscriptionId) {
        log.debug("Obteniendo domicilio de usuario STANDARD userId={}", userId);

        Map<String, Object> userData = getUserInfo(userId, subscriptionId);
        Map<String, Object> result = new HashMap<>();

        if (!userData.isEmpty()) {
            if (userData.containsKey("manzana")) result.put("manzana", userData.get("manzana"));
            if (userData.containsKey("lote")) result.put("lote", userData.get("lote"));

            String nombre = (String) userData.getOrDefault("nombre", "");
            String apellido = (String) userData.getOrDefault("apellido", "");
            result.put("nombreCompleto", (nombre + " " + apellido).trim());
        }

        return result;
    }

    // ===== MÉTODOS PRIVADOS DE APOYO =====

    private void updateUserFields(UserStandard user, UpdateStandardCommand command) {
        if (command.nombre() != null) user.setNombre(command.nombre());
        if (command.apellido() != null) user.setApellido(command.apellido());
        if (command.internalType() != null) user.setInternalType(command.internalType());
        if (command.numeroDocumento() != null) user.setNumeroDocumento(command.numeroDocumento());
        if (command.establecimientoId() != null) user.setEstablecimientoId(command.establecimientoId());
        if (command.manzana() != null) user.setManzana(command.manzana());
        if (command.lote() != null) user.setLote(command.lote());
        if (command.telefono() != null) user.setTelefono(command.telefono());
        if (command.isActive() != null) user.setIsActive(command.isActive());
    }

    private UserFilterStandard convertToStandardFilter(String subscriptionId, Object filter) {
        if (filter instanceof UserFilterStandard standardFilter) {
            return standardFilter;
        }

        if (filter != null) {
            log.warn("Tipo de filtro no compatible para UserStandard: {}. Usando filtro vacío.",
                    filter.getClass().getSimpleName());
        }

        return new UserFilterStandard(
                subscriptionId,
                null, null,null, null, null, null,
                null, null, null, null, null,
                null, null, null, null, null, null,
                null, null
        );
    }

    private Map<String, Object> buildPaginationInfo(Page<?> page) {
        Map<String, Object> paginationInfo = new HashMap<>();
        paginationInfo.put("page", page.getNumber());
        paginationInfo.put("size", page.getSize());
        paginationInfo.put("totalElements", page.getTotalElements());
        paginationInfo.put("totalPages", page.getTotalPages());
        paginationInfo.put("first", page.isFirst());
        paginationInfo.put("last", page.isLast());
        paginationInfo.put("hasNext", page.hasNext());
        paginationInfo.put("hasPrevious", page.hasPrevious());
        return paginationInfo;
    }

    private void invalidateUserCache(Long userId) {
        try {
            String pattern = CACHE_KEY_PREFIX + userId + "*";
            cachePort.invalidatePattern(pattern);
        } catch (Exception e) {
            log.warn("Error invalidando cache para userId={}: {}", userId, e.getMessage());
        }
    }

}