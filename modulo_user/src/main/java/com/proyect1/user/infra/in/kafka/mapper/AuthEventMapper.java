package com.proyect1.user.infra.in.kafka.mapper;

import com.proyect1.user.infra.in.kafka.dto.AuthEventDto;
import com.proyect1.user.application.kafka.input.AuthEvent;
import com.proyect1.user.application.kafka.input.EventType;

public class AuthEventMapper {

    public static AuthEvent from(AuthEventDto dto) {
        return new AuthEvent(
                dto.getEventId(),
                EventType.fromString(dto.getEventType()),
                dto.getUserId(),
                dto.getSuscriptionId(),
                dto.getUsername(),
                dto.getEmail(),
                dto.getIpAddress(),
                dto.getUserAgent(),
                dto.getTimestamp(),
                dto.getMetadata(),
                dto.getUserMetadata()
        );
    }
}
