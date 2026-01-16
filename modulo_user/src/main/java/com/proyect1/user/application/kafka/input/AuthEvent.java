package com.proyect1.user.application.kafka.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;


public record AuthEvent(
        @JsonProperty("event_id")
        String eventId,
        @JsonProperty("event_type")
        EventType type,
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("suscription_id")
        String SuscriptionId,
        String username,
        String email,
        @JsonProperty("ip_address")
        String ipAddress,
        @JsonProperty("user_agent")
        String userAgent,
        LocalDateTime timestamp,
        String metaData,
        JsonNode userMetaData
) {}
