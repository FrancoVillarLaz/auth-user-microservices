package org.proyect1.dto.auth.response;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ValidateTokenResponse {
    public boolean valid;

    public ValidateTokenResponse(boolean valid ) {
        this.valid = valid;
    }
}
