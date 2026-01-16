package org.proyect1.dto.subscription.response;

import java.util.Set;

public class ValidationErrorResponse {
    public String message;
    public Set<Long> unauthorizedSubscriptions;

    public ValidationErrorResponse(String message, Set<Long> unauthorizedSubscriptions) {
        this.message = message;
        this.unauthorizedSubscriptions = unauthorizedSubscriptions;
    }
}
