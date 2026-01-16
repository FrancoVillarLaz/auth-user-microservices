package org.proyect1.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.proyect1.dto.subscription.UserSubscriptionDTO;
import org.proyect1.dto.subscription.internal.SubscriptionValidationResult;
import org.proyect1.dto.subscription.response.SubscriptionCreatedResponse;
import org.proyect1.entity.UserSubscription;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.proyect1.entity.UserSubscription.activate;

@ApplicationScoped
public class SubscriptionService {

    private static final Logger LOG = Logger.getLogger(SubscriptionService.class);

    public UserSubscription getUserSubscription(Long userId, String subscriptionId) {
        return UserSubscription.findByUserAndSub(userId, subscriptionId).orElse(null);
    }

    @Transactional
    public SubscriptionCreatedResponse createUserSubscription(Long userId, String subscriptionId) {
        try {

            UserSubscription existingSub = UserSubscription.findByUserAndSub(userId, subscriptionId).orElse(null);

            if (existingSub != null) {
                if (existingSub.isActive) {
                    // 1. Caso: Ya existe y está activo
                    UserSubscriptionDTO existingDto = UserSubscriptionDTO.fromEntity(existingSub);
                    return SubscriptionCreatedResponse.alreadyActive(existingDto); // Se incluye el DTO existente

                } else {
                    // 2. Caso: Reactivado
                    activate(userId,  subscriptionId);

                    // Mapear la entidad actualizada
                    UserSubscriptionDTO reactivatedDto = UserSubscriptionDTO.fromEntity(existingSub);
                    return SubscriptionCreatedResponse.reactivated(reactivatedDto);
                }
            }

            // 3. Caso: Creado
            UserSubscription newSub = new UserSubscription();

            // hacer peticion al modulo en el puerto 8083 para crear usuario inncome


            newSub.userId = userId;
            newSub.idSuscripcion = subscriptionId;
            newSub.isActive = true;
            newSub.persist();

            // Mapear la nueva entidad (ya tiene el ID)
            UserSubscriptionDTO newDto = UserSubscriptionDTO.fromEntity(newSub);
            return SubscriptionCreatedResponse.created(newDto);

        } catch (Exception e) {
            LOG.errorf(e, "Error al crear suscripción %s para el usuario %d", subscriptionId, userId);

            // 4. Caso: Error inesperado
            // Ahora se usa el método 'error' que puede recibir un mensaje descriptivo
            return SubscriptionCreatedResponse.error("Error interno del servidor. Por favor, inténtelo más tarde.");
        }
    }
    /**
     * Valida si el usuario tiene las suscripciones requeridas
     */
    public SubscriptionValidationResult validateUserSubscriptions(Long userId, List<Long> requestedSubs) {

        if (requestedSubs == null || requestedSubs.isEmpty()) {
            return SubscriptionValidationResult.valid(userId, Set.of());
        }

        Set<String> userActiveSubs = UserSubscription.getActiveSubscriptions(userId)
                .stream()
                .collect(Collectors.toSet());

        Set<Long> requested = Set.copyOf(requestedSubs);

        Set<Long> unauthorized = requested.stream()
                .filter(sub -> !userActiveSubs.contains(sub))
                .collect(Collectors.toSet());

        if (!unauthorized.isEmpty()) {
            LOG.warnf("Usuario %d intentó acceder a suscripciones no permitidas: %s", userId, unauthorized);
            return SubscriptionValidationResult.error("Suscripciones no autorizadas", unauthorized);
        }

        return SubscriptionValidationResult.valid(userId, requested);
    }

    public boolean activateSubscription(Long userId, String subscriptionId) {
        try {
            activate(userId, subscriptionId);
            return true;
        } catch (Exception e) {
            LOG.errorf(e, "Error al activar suscripción %s para el usuario %d", subscriptionId, userId);
            return false;
        }

    }
    /**
     * Retorna lista de suscripciones activas del usuario
     */
    public Set<String> getUserSubscriptions(Long userId) {
        return UserSubscription.getActiveSubscriptions(userId)
                .stream()
                .collect(Collectors.toSet());
    }

    public boolean userHasSubscriptionActive(Long userId, String subscriptionId) {
        return UserSubscription.hasSubscriptionActive(userId, subscriptionId);
    }

    public boolean userHasSubscriptionDesactive(Long userId, String subscriptionId) {
        return UserSubscription.hasSubscriptionDesactive(userId, subscriptionId);
    }
}
