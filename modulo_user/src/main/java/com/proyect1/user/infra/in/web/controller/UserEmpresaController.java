package com.proyect1.user.infra.in.web.controller;

import com.proyect1.user.ports.in.UserSpecificUseCase;
import com.proyect1.user.shared.exception.SubscriptionNotSupportedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/user-service/empresa")
@RequiredArgsConstructor
public class UserEmpresaController {

    private final UserSpecificUseCase userService;

    /**
     *  Endpoint específico para buscar empresa por CUIT
     */
    @GetMapping("/by-cuit/{cuit}")
    public ResponseEntity<?> getEmpresaByCuit(
            @PathVariable String cuit,
            @RequestParam("subscriptionId") String subscriptionId) {

        log.info("Buscando empresa por CUIT={}, subscriptionId={}", cuit, subscriptionId);

        try {
            Map<String, Object> empresaInfo = userService
                    .getEmpresaByCuit(cuit, subscriptionId);

            if (empresaInfo.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(empresaInfo);

        } catch (SubscriptionNotSupportedException e) {
            log.warn("Error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            log.error("Error al buscar empresa por CUIT", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

    /**
     * Endpoint para obtener empresa por ID
     */
    @GetMapping("/{empresaId}")
    public ResponseEntity<?> getEmpresaById(
            @PathVariable Long empresaId,
            @RequestParam("subscriptionId") String subscriptionId) {

        log.info("Buscando empresa por ID={}, subscriptionId={}", empresaId, subscriptionId);

        try {
            Map<String, Object> empresaInfo = userService
                    .getEmpresaById(empresaId, subscriptionId);

            if (empresaInfo.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(empresaInfo);

        } catch (SubscriptionNotSupportedException e) {
            log.warn("Error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            log.error("Error al buscar empresa por ID", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
}