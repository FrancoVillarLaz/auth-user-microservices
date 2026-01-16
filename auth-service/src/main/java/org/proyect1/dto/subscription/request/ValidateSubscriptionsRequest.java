package org.proyect1.dto.subscription.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class ValidateSubscriptionsRequest {

    @NotNull
    public List<Long> idSuscripciones;
}
