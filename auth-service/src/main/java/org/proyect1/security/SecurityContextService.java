package org.proyect1.security;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

@RequestScoped
public class SecurityContextService {

    @Inject
    JsonWebToken jwt;

    public Long getUserId() {
        if (jwt == null || jwt.getClaim("userId") == null) {
            return null;
        }
        return Long.valueOf(jwt.getClaim("userId").toString());
    }

    public String getRole() {
        return jwt.getClaim("role") != null ? jwt.getClaim("role").toString() : "USER";
    }

    public String getUsername() {
        return jwt.getName(); // viene del claim `upn` o `preferred_username`
    }

    public String getIssuer() {
        return jwt.getIssuer();
    }

    public boolean isAuthenticated() {
        return jwt != null && jwt.getClaimNames() != null;
    }
}
