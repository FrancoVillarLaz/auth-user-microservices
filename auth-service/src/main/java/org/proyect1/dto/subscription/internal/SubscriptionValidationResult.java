package org.proyect1.dto.subscription.internal;

import java.util.Set;

public class SubscriptionValidationResult {

    public boolean valid;
    public String errorMessage;
    public Set<Long> unauthorizedSubscriptions;
    public Set<Long> validSubscriptions;

    private SubscriptionValidationResult() {}

    public static SubscriptionValidationResult valid(Long userId, Set<Long> validSubs) {
        SubscriptionValidationResult res = new SubscriptionValidationResult();
        res.valid = true;
        res.validSubscriptions = validSubs;
        return res;
    }

    public static SubscriptionValidationResult error(String message, Set<Long> unauthorized) {
        SubscriptionValidationResult res = new SubscriptionValidationResult();
        res.valid = false;
        res.errorMessage = message;
        res.unauthorizedSubscriptions = unauthorized;
        return res;
    }
}
