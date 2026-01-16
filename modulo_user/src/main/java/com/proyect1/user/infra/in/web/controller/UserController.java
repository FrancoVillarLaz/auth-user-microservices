package com.proyect1.user.infra.in.web.controller;

import com.proyect1.user.application.command.standard.CreateStandardCommand;
import com.proyect1.user.application.command.standard.UpdateStandardCommand;
import com.proyect1.user.application.mapper.CreateUserCommandMapper;
import com.proyect1.user.application.mapper.UpdateUserCommandMapper;
import com.proyect1.user.infra.in.web.dto.Request.CreateUserRequest;
import com.proyect1.user.infra.in.web.dto.Request.UpdateUserRequest;
import com.proyect1.user.ports.in.UserCrudUseCase;
import com.proyect1.user.shared.exception.SubscriptionNotSupportedException;
import com.proyect1.user.shared.exception.SuscriptionNotActiveException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.proyect1.user.shared.Helpers.buildFilterFromParams;

@Slf4j
@RestController
@RequestMapping("/api/user-service")
@RequiredArgsConstructor
public class UserController {

    private final UserCrudUseCase userService;

    /**
     *  Endpoint genérico para todos los tipos de suscripción
     * Soporta: STANDARD, EMPRESA, etc.
     */
    @GetMapping("/by-subscription/")
    public ResponseEntity<?> getAllUsersBySubscription(
            @RequestParam("subscriptionId") String subscriptionId,
            @RequestParam(required = false) Map<String, String> allParams,
            @PageableDefault(sort = "id") Pageable pageable) {

        log.info("Obteniendo usuarios para subscriptionId={}", subscriptionId);

        try {
            // Convertir parámetros al filtro apropiado según el tipo de suscripción
            Object filter = buildFilterFromParams(subscriptionId, allParams);

            Map<String, Object> result = userService
                    .getAllUsers(subscriptionId, filter, pageable);

            return ResponseEntity.ok(result);

        } catch (SubscriptionNotSupportedException e) {
            log.warn("Tipo de suscripción no soportado: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            log.error("Error al obtener usuarios por suscripción", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }



    /**
     * Endpoint para obtener información de usuario por suscripciones
     */
    @GetMapping("/by-subscription/info")
    public ResponseEntity<?> getUserInfo(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam("subscriptionId") String idSuscripcion) {

        if (idSuscripcion == null || idSuscripcion.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Debe proporcionar un id_suscripcion"));
        }

        log.info("Request user info userId={}, suscripcion={}", userId, idSuscripcion);

        try {
            Map<String, Object> userInfo = userService
                    .getUserInfo(userId, idSuscripcion);

            return ResponseEntity.ok(userInfo);

        } catch (SubscriptionNotSupportedException | SuscriptionNotActiveException e) {
            log.warn("Error de suscripción: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            log.error("Error en getUserInfo", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

    /**
     * CREATE - Crear un nuevo usuario
     */
    @PostMapping
    public ResponseEntity<?> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        log.info("Creando usuario: {} {}", request.getNombre(), request.getApellido());

        try {
            CreateStandardCommand command = CreateUserCommandMapper.fromRequest(request);

            String subscriptionId = request.getSubscriptionId();

            Map<String, Object> createdUser = userService.createUser(command, subscriptionId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(createdUser);

        } catch (IllegalArgumentException e) {
            log.warn("Validación fallida al crear usuario: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            log.error("Error al crear usuario", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

    /**
     * UPDATE - Actualizar un usuario existente
     */
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(
            @RequestHeader("X-User-Id") Long jwtUserId,
            @RequestHeader("X-Role") String role,
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest request) {

        log.info("Actualizando usuario ID={}", userId);

        if (!userId.equals(jwtUserId) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "No autorizado"));
        }

        try {
            UpdateStandardCommand command = UpdateUserCommandMapper.fromRequest(request);

            String subscriptionId = request.getSubscriptionId();

            Map<String, Object> updatedUser = userService.updateUser(userId, command, subscriptionId);

            return ResponseEntity.ok(updatedUser);

        } catch (IllegalArgumentException e) {
            log.warn("Validación fallida al actualizar usuario: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            log.error("Error al actualizar usuario", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

    /**
     * DELETE LÓGICO - Desactivar usuario
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deactivateUser(
            @PathVariable Long userId,
            @RequestParam(value = "subscriptionId", defaultValue = "STANDARD") String subscriptionId) {

        log.info("Desactivando usuario ID={}, subscription={}", userId, subscriptionId);

        try {
            userService.deactivateUser(userId, subscriptionId);
            return ResponseEntity.ok(
                    Map.of("message", "Usuario desactivado exitosamente")
            );

        } catch (IllegalArgumentException e) {
            log.warn("Error al desactivar usuario: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            log.error("Error al desactivar usuario", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

    /**
     * REACTIVAR - Activar usuario desactivado
     */
    @PatchMapping("/{userId}/activate")
    public ResponseEntity<?> activateUser(
            @PathVariable Long userId,
            @RequestParam(value = "subscriptionId", defaultValue = "STANDARD") String subscriptionId) {

        log.info("Activando usuario ID={}, subscription={}", userId, subscriptionId);

        try {
            userService.activateUser(userId, subscriptionId);
            return ResponseEntity.ok(
                    Map.of("message", "Usuario activado exitosamente")
            );

        } catch (IllegalArgumentException e) {
            log.warn("Error al activar usuario: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            log.error("Error al activar usuario", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

    /**
     * Health check
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "User Service",
                "architecture", "Hexagonal"
        ));
    }
}
