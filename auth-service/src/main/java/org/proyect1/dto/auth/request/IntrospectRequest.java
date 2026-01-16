package org.proyect1.dto.auth.request;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotBlank;

@RegisterForReflection
public class IntrospectRequest {
    @NotBlank
    public String Token;
}
