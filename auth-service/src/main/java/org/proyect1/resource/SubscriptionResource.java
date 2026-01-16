package org.proyect1.resource;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.proyect1.dto.auth.response.*;
import org.proyect1.dto.subscription.request.ValidateSubscriptionsRequest;
import org.proyect1.dto.subscription.response.*;
import org.proyect1.security.SecurityUser;
import org.proyect1.service.SubscriptionService;
import org.jboss.logging.Logger;


/**
 * Endpoint para validación de suscripciones
 * Usado por el Gateway antes de llamar al User Service
 */
@Path("/subscriptions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SubscriptionResource {

    private static final Logger LOG = Logger.getLogger(SubscriptionResource.class);

    @Inject
    SubscriptionService SubscriptionService;

    // Inyectamos SecurityContext en lugar de JsonWebToken para la identidad
    @Context
    SecurityContext securityContext;

    // Método auxiliar para obtener el usuario autenticado
    private SecurityUser getSecurityUser() {
        if (securityContext == null || securityContext.getUserPrincipal() == null) {
            throw new NotAuthorizedException("No autenticado");
        }
        return (SecurityUser) securityContext.getUserPrincipal();
    }

    /**
     * Crea una suscripción para el usuario autenticado
     *
     * POST /api/auth/subscriptions/create/{subscriptionId}
     * Authorization: Bearer {JWT}
     *
     */
    @POST
    @Path("/create/{subscriptionId}")
    @RolesAllowed({"USER", "ADMIN"})
    public SubscriptionCreatedResponse createUserSubscription(Long userId, String subscriptionId) {
        return SubscriptionService.createUserSubscription(userId, subscriptionId);
    }
    /**
     * Valida que el usuario tenga acceso a las suscripciones solicitadas
     *
     * POST /api/auth/subscriptions/validate
     * Authorization: Bearer {JWT}
     * Body: { "idSuscripciones": ["sub1", "sub2"] }
     *
     * Usado por el Gateway para validar antes de llamar a User Service
     */
    @POST
    @Path("/validate")
    @RolesAllowed({"USER", "ADMIN"})
    public Response validateSubscriptions(@Valid ValidateSubscriptionsRequest request) {
        try {
            SecurityUser user = getSecurityUser();
            Long userId = user.id();
            String username = user.username();

            LOG.infof("Gateway solicitó validación de suscripciones para usuario %s (ID: %d): %s",
                    username, userId, request.idSuscripciones);

            var validation = SubscriptionService.validateUserSubscriptions(
                    userId,
                    request.idSuscripciones
            );

            if (!validation.valid) {
                LOG.warnf("Validación fallida para usuario %d: %s", userId, validation.errorMessage);
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(new ValidationErrorResponse(
                                validation.errorMessage,
                                validation.unauthorizedSubscriptions
                        ))
                        .build();
            }

            // Retornar validación exitosa
            LOG.infof("Validación exitosa para usuario %d", userId);
            return Response.ok(new ValidationSuccessResponse(
                    userId,
                    username,
                    validation.validSubscriptions
            )).build();

        } catch (IllegalStateException | NotAuthorizedException e) {
            LOG.errorf("Error de autenticación: %s", e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new MessageResponse(
                            "Error de autenticación: " + e.getMessage()
                    ))
                    .build();
        } catch (Exception e) {
            LOG.errorf(e, "Error inesperado validando suscripciones");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MessageResponse(
                            "Error interno del servidor"
                    ))
                    .build();
        }
    }

    /**
     * Lista las suscripciones activas del usuario
     * Útil para el Gateway o frontend
     */
    @GET
    @Path("/my-subscriptions")
    @RolesAllowed({"USER", "ADMIN"})
    public Response getMySubscriptions() {
        try {
            // Obtenemos los datos del SecurityUser
            SecurityUser user = getSecurityUser();
            Long userId = user.id();
            String username = user.username();

            LOG.infof("Usuario %s (ID: %d) solicitó sus suscripciones", username, userId);
            var subscriptions = SubscriptionService.getUserSubscriptions(userId);

            return Response.ok(new MySubscriptionsResponse(
                    userId,
                    username,
                    subscriptions
            )).build();

        } catch (Exception e) {
            LOG.errorf(e, "Error obteniendo suscripciones del usuario");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MessageResponse(
                            "Error al obtener suscripciones"
                    ))
                    .build();
        }
    }

    /**
     * Verifica si el usuario tiene una suscripción específica
     * Útil para checks rápidos en el Gateway
     */
    @GET
    @Path("/check/{idSuscripcion}")
    @RolesAllowed({"USER", "ADMIN"})
    public Response checkSubscription(@PathParam("idSuscripcion") String idSuscripcion) {
        try {
            Long userId = getSecurityUser().id();

            boolean hasAccess = SubscriptionService.userHasSubscriptionActive(
                    userId,
                    idSuscripcion
            );

            return Response.ok(new SubscriptionCheckResponse(
                    userId,
                    idSuscripcion,
                    hasAccess
            )).build();
        } catch (Exception e) {
            LOG.errorf(e, "Error verificando suscripción");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

}