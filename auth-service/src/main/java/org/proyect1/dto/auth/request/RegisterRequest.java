package org.proyect1.dto.auth.request;

import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@RegisterForReflection
public class RegisterRequest {

    @NotBlank(message = "Username es requerido")
    @Size(min = 3, max = 50, message = "Username debe tener entre 3 y 50 caracteres")
    public String username;

    @NotBlank(message = "Email es requerido")
    @Email(message = "Email debe ser válido")
    public String email;

    @NotBlank(message = "Password es requerido")
    @Size(min = 6, message = "Password debe tener al menos 6 caracteres")
    public String password;

    @NotNull(message = "SuscriptionId es requerido")
    public String suscriptionId;

    public JsonNode userMetaData;

}
