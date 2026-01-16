package org.proyect1.security;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.proyect1.dto.auth.response.MessageResponse;
import org.proyect1.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Provider
@Priority(Priorities.AUTHENTICATION - 100)
public class TokenBlacklistFilter implements ContainerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(TokenBlacklistFilter.class);

    @Inject
    RedisService redisService;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        String token = authHeader.substring(7);

        try {
            if (redisService.isTokenBlacklisted(token)) {
                log.warn("Intento de acceso con Token revocado: {}", token);
                requestContext.abortWith(
                        Response.status(Response.Status.UNAUTHORIZED)
                                .entity(new MessageResponse("Token revocado"))
                                .build()
                );
            }
        } catch (Exception e) {
            log.error("Error al verificar Token en blacklist", e);
            requestContext.abortWith(
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(new MessageResponse("Error de seguridad interno"))
                            .build()
            );
        }
    }
}