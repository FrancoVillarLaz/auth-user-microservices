package org.proyect1.dto.auth.response;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class MessageResponse {
    public String message;

    public MessageResponse(String message) {
        this.message = message;
    }
}