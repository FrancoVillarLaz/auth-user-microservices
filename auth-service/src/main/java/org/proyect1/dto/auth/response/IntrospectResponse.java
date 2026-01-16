package org.proyect1.dto.auth.response;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class IntrospectResponse {
    public boolean active;
    public Long userId;
    public String role;

    public IntrospectResponse(boolean active, Long userId, String role) {
        this.active = active;
        this.userId = userId;
        this.role = role;
    }
}

