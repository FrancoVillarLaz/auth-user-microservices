package com.proyect1.user.infra.in.kafka.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


/**
 * DTO para eventos de Kafka desde Auth Service
 * Este DTO es específico del adapter, no toca el dominio
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthEventDto {

    @JsonProperty("event_id")
    private String eventId;

    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("suscription_id")
    private String suscriptionId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("ip_address")
    private String ipAddress;

    @JsonProperty("user_agent")
    private String userAgent;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
    @JsonProperty("user_metadata")
    private JsonNode userMetadata;
    @JsonProperty("metadata")
    private String metadata;

}