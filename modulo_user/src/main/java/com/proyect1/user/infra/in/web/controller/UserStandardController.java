package com.proyect1.user.infra.in.web.controller;

import com.proyect1.user.ports.in.UserSpecificUseCase;
import com.proyect1.user.shared.exception.SubscriptionNotSupportedException;
import com.proyect1.user.shared.exception.SuscriptionNotActiveException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

        import java.util.Map;

import static com.proyect1.user.shared.Helpers.buildFilterFromParams;

@Slf4j
@RestController
@RequestMapping("/api/user-service/standard")
@RequiredArgsConstructor
public class UserStandardController {

    private final UserSpecificUseCase userService;

        /**
         *  Endpoint genérico para todos los tipos de suscripción
         * Soporta: STANDARD, EMPRESA, etc.
         */
        @GetMapping("/by-subscription/info/admin")
        public ResponseEntity<?> getAllUsersBySubscription(
                @RequestParam("subscriptionId") String subscriptionId,
                @RequestParam(required = false) Map<String, String> allParams,
                @PageableDefault(sort = "id") Pageable pageable) {

            log.info("Obteniendo usuarios para subscriptionId={}", subscriptionId);

            try {
                // Convertir parámetros al filtro apropiado según el tipo de suscripción
                Object filter = buildFilterFromParams(subscriptionId, allParams);

                Map<String, Object> result = userService
                        .getAllUsersWithEmail(subscriptionId, filter, pageable);

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
     * Endpoint para obtener información de la Manzana y Lote de un usuario
     * (tambien proporciona el nombre completo por fines practicos)
     */
    @GetMapping("/by-subscription/info/domicilio")
    public ResponseEntity<?> getUserInfo(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam("idSuscripcion") String idSuscripcion) {

        if (idSuscripcion == null || idSuscripcion.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Debe proporcionar un id_suscripcion"));
        }

        log.info("Request user info userId={}, suscripcion={}", userId, idSuscripcion);

        try {
            Map<String, Object> result = userService.getUserStandardDomicilio(userId, idSuscripcion);
            return ResponseEntity.ok(result);

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

}