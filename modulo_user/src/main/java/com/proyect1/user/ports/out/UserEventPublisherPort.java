package com.proyect1.user.ports.out;

import com.proyect1.user.application.kafka.events.UserCreatedEvent;
import com.proyect1.user.application.kafka.events.UserCreationFailedEvent;

public interface UserEventPublisherPort {

    void publishUserCreated(UserCreatedEvent event);

    void publishUserCreationFailed(UserCreationFailedEvent event);
}
