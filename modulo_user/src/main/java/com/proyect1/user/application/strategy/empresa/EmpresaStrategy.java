package com.proyect1.user.application.strategy.empresa;

import com.proyect1.user.application.strategy.core.UserCrudStrategy;
import com.proyect1.user.domain.model.empresa.UserEmpresa;
import com.proyect1.user.domain.model.empresa.UserFilterEmpresa;
import com.proyect1.user.ports.out.CachePort;
import com.proyect1.user.ports.out.CondicionIvaRepositoryPort;
import com.proyect1.user.ports.out.UserEmpresaRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import com.proyect1.user.application.command.empresa.CreateEmpresaCommand;
import com.proyect1.user.application.command.empresa.UpdateEmpresaCommand;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmpresaStrategy implements UserCrudStrategy, EmpresaSpecificStrategy {

    private final UserEmpresaRepositoryPort repositoryPort;
    private final CachePort cachePort;
    private final CondicionIvaRepositoryPort condicionIvaRepositoryPort;

    private static final String CACHE_KEY_PREFIX = "empresa:";
    private static final long CACHE_TTL_SECONDS = TimeUnit.MINUTES.toSeconds(5);


    @Override
    public Map<String, Object> createUser(Object command, String subscriptionId) {
        if (!(command instanceof CreateEmpresaCommand empresaCommand)) {
            throw new IllegalArgumentException(
                    "Para suscripción EMPRESA se requiere CreateEmpresaCommand. Recibido: " +
                            (command != null ? command.getClass().getSimpleName() : "null")
            );
        }

        log.info("Creando empresa con CUIT: {}", empresaCommand.cuit());

        log.debug("esta completa la empresa? veamos:" + empresaCommand.condicionIvaId() + " " +
                empresaCommand.razonSocial() + " " + empresaCommand.cuit() + " " + empresaCommand.telefono() + " " +
                empresaCommand.condicionIvaId() + " "+ empresaCommand.internalType() + " " + empresaCommand.userId()
        );

        // Validar CUIT único
        repositoryPort.findUserEmpresaByCuit(empresaCommand.cuit())
                .ifPresent(e -> {
                    throw new IllegalArgumentException(
                            "Ya existe una empresa con el CUIT: " + empresaCommand.cuit()
                    );
                });

        UserEmpresa empresa = empresaCommand.toDomain();

        log.debug("esta completa la empresa? veamos:" + empresa.toString());

        empresa.setCondicionFrenteIva(
                condicionIvaRepositoryPort.findById(empresa.getCondicionFrenteIva().getId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "No se encontró la condición frente al IVA con ID: " +
                                        empresa.getCondicionFrenteIva().getId()
                        ))
        );

        UserEmpresa savedEmpresa = repositoryPort.saveUserEmpresa(empresa);
        invalidateEmpresaCache(savedEmpresa.getUserAuthId());

        log.info("Empresa creada exitosamente con ID: {}", savedEmpresa.getUserAuthId());
        return savedEmpresa.toMap();
    }

    @Override
    public Map<String, Object> getUserInfo(Long userId, String subscriptionId) {
        final String cacheKey = CACHE_KEY_PREFIX + userId + ":" + subscriptionId;

        Optional<Map<String, Object>> cached = cachePort.get(cacheKey);
        if (cached.isPresent()) {
            log.debug("Cache HIT para empresa userId={}, subscription={}", userId, subscriptionId);
            return cached.get();
        }

        log.debug("Cache MISS para empresa userId={}, subscription={}. Consultando repositorio...",
                userId, subscriptionId);

        Optional<UserEmpresa> domainModel = repositoryPort.findUserEmpresaByUserId(userId);
        if (domainModel.isEmpty()) {
            log.warn("No se encontró información de UserEmpresa para userId={}", userId);
            return Map.of();
        }


        Map<String, Object> data = domainModel.get().toMap();
        cachePort.put(cacheKey, data, CACHE_TTL_SECONDS);

        return data;
    }

    @Override
    public Map<String, Object> getAllUsers(String subscriptionId, Object filter, Pageable pageable) {
        log.debug("Obteniendo todas las empresas para subscription={}", subscriptionId);

        Map<String, Object> result = new HashMap<>();
        UserFilterEmpresa empresaFilter = convertToEmpresaFilter(subscriptionId, filter);

        if (pageable != null) {
            Page<UserEmpresa> empresasPage = repositoryPort.findAllWithFilters(empresaFilter, pageable);

            List<Map<String, Object>> content = empresasPage.getContent().stream()
                    .map(UserEmpresa::toMap)
                    .collect(Collectors.toList());

            result.put("content", content);
            result.put("pagination", buildPaginationInfo(empresasPage));
        } else {
            List<UserEmpresa> empresas = repositoryPort.findAllWithFilters(empresaFilter);
            List<Map<String, Object>> empresaData = empresas.stream()
                    .map(UserEmpresa::toMap)
                    .collect(Collectors.toList());

            result.put("content", empresaData);
            result.put("total", empresaData.size());
        }

        return result;
    }

    @Override
    public Map<String, Object> updateUser(Long userId, Object command, String subscriptionId) {
        if (!(command instanceof UpdateEmpresaCommand updateCommand)) {
            throw new IllegalArgumentException(
                    "Para suscripción EMPRESA se requiere UpdateEmpresaCommand. Recibido: " +
                            (command != null ? command.getClass().getSimpleName() : "null")
            );
        }

        log.info("Actualizando empresa ID: {}", userId);

        UserEmpresa existingEmpresa = repositoryPort.findUserEmpresaByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró la empresa con userId: " + userId
                ));

        // Validar CUIT único si se está cambiando
        if (updateCommand.cuit() != null &&
                !updateCommand.cuit().equals(existingEmpresa.getCuit())) {

            repositoryPort.findUserEmpresaByCuit(updateCommand.cuit())
                    .ifPresent(e -> {
                        throw new IllegalArgumentException(
                                "Ya existe otra empresa con el CUIT: " + updateCommand.cuit()
                        );
                    });
        }

        // Actualizar campos
        updateEmpresaFields(existingEmpresa, updateCommand);
        existingEmpresa.setUpdatedAt(LocalDateTime.now());

        UserEmpresa updatedEmpresa = repositoryPort.saveUserEmpresa(existingEmpresa);
        invalidateEmpresaCache(userId);

        log.info("Empresa actualizada exitosamente: {}", userId);
        return updatedEmpresa.toMap();
    }

    @Override
    public Map<String, Object> deactivateUser(Long userId, String subscriptionId) {
        log.info("Desactivando empresa ID: {}", userId);

        UserEmpresa empresa = repositoryPort.findUserEmpresaByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró la empresa con userId: " + userId
                ));

        empresa.setIsActive(false);
        empresa.setUpdatedAt(LocalDateTime.now());
        repositoryPort.saveUserEmpresa(empresa);

        log.info("Empresa desactivada exitosamente: {}", userId);
        invalidateEmpresaCache(userId);

        return Map.of(
                "userId", userId,
                "status", "DEACTIVATED",
                "message", "Empresa desactivada exitosamente",
                "timestamp", LocalDateTime.now()
        );
    }

    @Override
    public Map<String, Object> activateUser(Long userId, String subscriptionId) {
        log.info("Activando empresa ID: {}", userId);

        UserEmpresa empresa = repositoryPort.findUserEmpresaByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró la empresa con userId: " + userId
                ));

        empresa.setIsActive(true);
        empresa.setUpdatedAt(LocalDateTime.now());
        repositoryPort.saveUserEmpresa(empresa);

        log.info("Empresa activada exitosamente: {}", userId);
        invalidateEmpresaCache(userId);

        return Map.of(
                "userId", userId,
                "status", "ACTIVATED",
                "message", "Empresa activada exitosamente",
                "timestamp", LocalDateTime.now()
        );
    }

    @Override
    public String getSupportedSubscriptionId(String sub) {
        return "EMPRESA".equalsIgnoreCase(sub) ? sub : null;
    }

    // ===== IMPLEMENTACIÓN DE EmpresaSpecificStrategy =====

    @Override
    public Map<String, Object> getEmpresaByCuit(String cuit, String subscriptionId) {
        log.debug("Buscando empresa por CUIT={}", cuit);

        final String cacheKey = CACHE_KEY_PREFIX + "cuit:" + cuit + ":" + subscriptionId;

        return cachePort.get(cacheKey)
                .map(cached -> {
                    log.debug("Cache HIT para empresa cuit={}", cuit);
                    return cached;
                })
                .orElseGet(() -> {
                    log.debug("Cache MISS para empresa cuit={}. Consultando repositorio...", cuit);

                    return repositoryPort.findUserEmpresaByCuit(cuit)
                            .map(empresa -> {
                                Map<String, Object> data = empresa.toMap();
                                cachePort.put(cacheKey, data, 300);
                                return data;
                            })
                            .orElse(Map.of());
                });
    }

    @Override
    public Map<String, Object> getEmpresaById(Long empresaId, String subscriptionId) {
        log.debug("Buscando empresa por ID={}", empresaId);

        final String cacheKey = CACHE_KEY_PREFIX + empresaId + ":" + subscriptionId;

        return cachePort.get(cacheKey)
                .map(cached -> {
                    log.debug("Cache HIT para empresa id={}", empresaId);
                    return cached;
                })
                .orElseGet(() -> {
                    log.debug("Cache MISS para empresa id={}. Consultando repositorio...", empresaId);

                    return repositoryPort.findUserEmpresaById(empresaId)
                            .map(empresa -> {
                                Map<String, Object> data = empresa.toMap();
                                cachePort.put(cacheKey, data, 300);
                                return data;
                            })
                            .orElse(Map.of());
                });
    }

    // ===== MÉTODOS PRIVADOS DE APOYO =====

    private void updateEmpresaFields(UserEmpresa empresa, UpdateEmpresaCommand command) {
        if (command.razonSocial() != null) empresa.setRazonSocial(command.razonSocial());
        if (command.cuit() != null) empresa.setCuit(command.cuit());
        if (command.telefono() != null) empresa.setTelefono(command.telefono());
        if (command.isActive() != null) empresa.setIsActive(command.isActive());
    }

    private UserFilterEmpresa convertToEmpresaFilter(String subscriptionId, Object filter) {
        if (filter instanceof UserFilterEmpresa empresaFilter) {
            return empresaFilter;
        }

        if (filter != null) {
            log.warn("Tipo de filtro no compatible para UserEmpresa: {}. Usando filtro vacío.",
                    filter.getClass().getSimpleName());
        }

        return new UserFilterEmpresa(
                subscriptionId,
                null,
                null, // cuit
                null, // razonSocial
                null, // userId
                null, // telefono
                null, // isActive
                null, // createdAtFrom
                null, // createdAtTo
                null, // updatedAtFrom
                null, // updatedAtTo
                null, // pageNumber
                null, // pageSize
                null, // sortBy
                null  // sortDirection
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

    private void invalidateEmpresaCache(Long userId) {
        try {
            String pattern = CACHE_KEY_PREFIX + userId + "*";
            cachePort.invalidatePattern(pattern);
        } catch (Exception e) {
            log.warn("Error invalidando cache para empresa userId={}: {}", userId, e.getMessage());
        }
    }
}