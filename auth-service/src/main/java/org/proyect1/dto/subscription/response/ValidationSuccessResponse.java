package org.proyect1.dto.subscription.response;

import java.util.Set;

public class ValidationSuccessResponse {
    public Long userId;
    public String username;
    public Set<Long> validSubscriptions;

    public ValidationSuccessResponse(Long userId, String username, Set<Long> validSubscriptions) {
        this.userId = userId;
        this.username = username;
        this.validSubscriptions = validSubscriptions;
    }
}
