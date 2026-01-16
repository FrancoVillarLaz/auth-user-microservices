package org.proyect1.resource;

import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.proyect1.dto.auth.request.IntrospectRequest;
import org.proyect1.dto.auth.request.LoginRequest;
import org.proyect1.dto.auth.request.RefreshRequest;
import org.proyect1.dto.auth.request.RegisterRequest;
import org.proyect1.dto.auth.response.*;
import org.proyect1.security.SecurityUser;
import org.proyect1.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    private static final Logger log = LoggerFactory.getLogger(AuthResource.class);
    @Inject
    AuthService authService;

    @Context
    SecurityContext securityContext;

    // Método auxiliar para obtener el usuario autenticado
    private SecurityUser getSecurityUser() {
        if (securityContext == null || securityContext.getUserPrincipal() == null) {
            throw new NotAuthorizedException("No autenticado");
        }
        return (SecurityUser) securityContext.getUserPrincipal();
    }


    @POST
    @Path("/register")
    @PermitAll
    public Response register(@Valid RegisterRequest request,
                             @HeaderParam("X-Forwarded-For") String ipAddress) {
        try {
            TokenResponse response = authService.register(request);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MessageResponse(e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/login")
    @PermitAll
    public Response login(@Valid LoginRequest request,
                          @HeaderParam("X-Forwarded-For") String ipAddress,
                          @HeaderParam("User-Agent") String userAgent) {
        try {
            TokenResponse response = authService.login(request, ipAddress, userAgent);
            return Response.ok(response).build();
        } catch (NotAuthorizedException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new MessageResponse(e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/refresh")
    @Authenticated
    public Response refresh(@Valid RefreshRequest request) {
        try {
            TokenResponse response = authService.refreshToken(request.refreshToken);
            return Response.ok(response).build();
        } catch (NotAuthorizedException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new MessageResponse(e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/logout")
    @RolesAllowed({"USER", "ADMIN"})
    public Response logout(@HeaderParam("Authorization") String authHeader,
                           @HeaderParam("X-Forwarded-For") String ipAddress,
                           @Valid RefreshRequest request) {
        try {
            SecurityUser user = getSecurityUser();
            Long userId = user.id();
            String username = user.username();

            String accessToken = null;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                accessToken = authHeader.substring(7);
            }

            authService.logout(userId.toString(), username, accessToken, request.refreshToken, ipAddress);
            return Response.ok(new MessageResponse("Logout exitoso")).build();
        } catch (NotAuthorizedException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new MessageResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MessageResponse("Error al cerrar sesión"))
                    .build();
        }
    }

    @Path("/me")
    @GET
    @Authenticated
    public Response getUserInfo(@Context SecurityContext context) {
        var principal = context.getUserPrincipal();
        return Response.ok(principal.getName()).build();
    }


    /* Introspect a Token to get its details
     * @param request IntrospectRequest containing the Token to introspect
     * @return Response with IntrospectResponse containing Token details
     */
    @POST
    @Path("/introspect")
    @Authenticated
    public Response introspect(@Valid IntrospectRequest request) {
        try {
            var parsed = authService.validarAndParser(request.Token);
            return Response.ok(new IntrospectResponse(true, parsed.userId, parsed.role)).build();
        } catch (NotAuthorizedException e) {
            return Response.ok(new IntrospectResponse(false, null, null)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new MessageResponse("Error interno: " + e.getMessage()))
                    .build();
        }
    }

    /*
     * Validate if a Token is valid or not
     * @param request IntrospectRequest containing the Token to validate
     * @return Response boolean indicating if the Token is valid
     */
    @POST
    @Path("/validate")
    @PermitAll
    public Response validate(@Context SecurityContext context, @Valid IntrospectRequest request) {
        try {
            boolean isValid = authService.validar(request.Token);

            if (isValid) {
                return Response.ok()
                        .entity(new ValidateTokenResponse(true))
                        .build();

            } else {
                throw new NotAuthorizedException("Token inválido");
            }

        } catch (NotAuthorizedException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ValidateTokenResponse(false))
                    .build();

        } catch (Exception e) {
            log.error("Error inesperado durante la validación del token", e);
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ValidateTokenResponse(false))
                    .build();
        }
    }

    @POST
    @Path("/validate-gateway")
    @PermitAll
    public Response validateGateway(@Context SecurityContext context, @Valid IntrospectRequest request) {
        try {
            var userId = context.getUserPrincipal().getName();
            log.info("Validando token para el usuario ID: " + userId);
            var parsed = authService.validarAndParser(request.Token);
            return Response.ok(new IntrospectResponse(true, parsed.userId, parsed.role)).build();
        }
        catch (NotAuthorizedException e) {
            return Response.ok(new IntrospectResponse(false, null, null)).build();
        }
        catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ValidateTokenResponse(false))
                    .build();
        }
    }

    @GET
    @Path("/health")
    @PermitAll
    public Response health() {
        return Response.ok(new MessageResponse("Auth service is running :)")).build();
    }
}