package com.proyect1.user.application.kafka.events;

import com.proyect1.user.application.command.CreateUserFromAuthCommand;
import com.proyect1.user.application.kafka.input.EventType;
import com.proyect1.user.domain.model.standard.UserStandard;
import com.proyect1.user.domain.model.empresa.UserEmpresa;
import lombok.Builder;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

@Builder
public record UserCreatedEvent(

        @JsonProperty("event_id")
        String eventId,

        @JsonProperty("event_type")
        EventType eventType,

        @JsonProperty("user_id")
        Long userId,

        @JsonProperty("username")
        String username,

        @JsonProperty("ip_address")
        String ipAddress,

        @JsonProperty("user_agent")
        String userAgent,

        @JsonProperty("timestamp")
        LocalDateTime timestamp,

        @JsonProperty("metadata")
        String metadata,

        @JsonProperty("email")
        String email,

        @JsonProperty("user_type")
        String userType,

        @JsonProperty("created_entity")
        String createdEntity // "STANDARD" o "EMPRESA"
) {
    public static UserCreatedEvent from(UserStandard user, CreateUserFromAuthCommand command) {
        return UserCreatedEvent.builder()
                .eventId(command.eventId())
                .eventType(EventType.USER_CREATION_SUCCESS)
                .userId(command.userId())
                .username(command.username())
                .email(command.email())
                .ipAddress(command.ipAddress())
                .userAgent("vamos viendo")
                .timestamp(LocalDateTime.now())
                .metadata("User STANDARD created from auth event")
                .userType(command.subscriptionId())
                .createdEntity("STANDARD")
                .build();
    }

    public static UserCreatedEvent from(UserEmpresa empresa, CreateUserFromAuthCommand command) {
        return UserCreatedEvent.builder()
                .eventId(command.eventId())
                .eventType(EventType.USER_CREATION_SUCCESS)
                .userId(command.userId())
                .username(command.username())
                .email(command.email())
                .ipAddress(command.ipAddress())
                .userAgent("vamos viendo")
                .timestamp(LocalDateTime.now())
                .metadata("Empresa created from auth event")
                .userType(command.subscriptionId())
                .createdEntity("EMPRESA")
                .build();
    }
}