package com.proyect1.user.application.command;

import com.proyect1.user.domain.model.standard.UserFilterStandard;

public record GetUsersBySubscriptionQuery(
        String subscriptionId,
        UserFilterStandard filter
) {}
