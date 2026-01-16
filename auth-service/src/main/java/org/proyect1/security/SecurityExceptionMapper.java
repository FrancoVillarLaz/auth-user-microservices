package org.proyect1.security;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.proyect1.dto.auth.response.MessageResponse;

@Provider
public class SecurityExceptionMapper implements ExceptionMapper<SecurityException> {
    @Override
    public Response toResponse(SecurityException e) {
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new MessageResponse(e.getMessage()))
                .build();
    }
}
