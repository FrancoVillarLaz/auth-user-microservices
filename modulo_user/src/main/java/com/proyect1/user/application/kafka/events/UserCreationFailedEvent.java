package com.proyect1.user.application.kafka.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.proyect1.user.application.command.CreateUserFromAuthCommand;
import com.proyect1.user.application.kafka.input.EventType;

import java.time.LocalDateTime;

public record UserCreationFailedEvent (

        @JsonProperty("event_id")
        String eventId,

        @JsonProperty("event_type")
        EventType eventType,

        @JsonProperty("user_id")
        Long userId,

        @JsonProperty("username")
        String username,

        @JsonProperty("suscription_id")
        String suscriptionId,

        @JsonProperty("reason")
        String reason,

        @JsonProperty("ip_address")
        String ipAddress,

        @JsonProperty("user_agent")
        String userAgent,

        @JsonProperty("timestamp")
        LocalDateTime timestamp,

        @JsonProperty("metadata")
        String metadata
)
{
    public static UserCreationFailedEvent from(CreateUserFromAuthCommand command, Exception ex) {
        return new UserCreationFailedEvent(
                command.eventId(),
                EventType.USER_CREATION_FAILED,
                command.userId(),
                command.username(),
                command.subscriptionId(), // AGREGADO
                ex.getMessage(),
                command.ipAddress(),
                null,
                LocalDateTime.now(),
                "Failed to create " + command.subscriptionId()
        );
    }
}