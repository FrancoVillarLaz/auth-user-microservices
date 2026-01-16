package org.proyect1.security;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtSecurityFilter implements ContainerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtSecurityFilter.class);

    @Inject
    JwtTokenParser jwtTokenParser;


    @Override
    public void filter(ContainerRequestContext requestContext) {
        String header = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            return;
        }

        String token = header.substring(7);

        try {
            // Se asume que TokenBlacklistFilter ya corrió y no abortó.

            SecurityUser user = jwtTokenParser.parseAndValidate(token);

            SecurityContext originalContext = requestContext.getSecurityContext();

            // Seteamos el contexto con el usuario validado
            requestContext.setSecurityContext(new SecurityContext() {
                @Override public SecurityUser getUserPrincipal() { return user; }
                @Override public boolean isUserInRole(String role) { return user.role().equals(role); }
                @Override public boolean isSecure() { return originalContext.isSecure(); }
                @Override public String getAuthenticationScheme() { return "Bearer"; }
            });
            log.info("Token claims -> user: {}, role: {}", user.username(), user.role());

        } catch (Exception e) {
            // Manejamos fallos de validación (firma, expiración, tipo incorrecto)
            log.warn("Auth error: {}", e.getMessage(), e); // Log con stack trace para debug
            abort(requestContext, Response.Status.UNAUTHORIZED, e.getMessage());
        }
    }

    private void abort(ContainerRequestContext ctx, Response.Status status, String msg) {
        ctx.abortWith(Response.status(status)
                .entity(new org.proyect1.dto.auth.response.MessageResponse(msg))
                .build()
        );
    }
}