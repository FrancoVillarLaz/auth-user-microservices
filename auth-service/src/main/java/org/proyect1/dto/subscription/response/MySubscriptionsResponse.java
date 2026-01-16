package org.proyect1.dto.subscription.response;

import java.util.Set;

public class MySubscriptionsResponse {

    public Long userId;
    public String username;
    public Set<String> subscriptions;

    public MySubscriptionsResponse(Long userId, String username, Set<String> subscriptions) {
        this.userId = userId;
        this.username = username;
        this.subscriptions = subscriptions;
    }
}
