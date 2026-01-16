package com.proyect1.user.infra.in.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyect1.user.infra.in.kafka.dto.AuthEventDto;
import com.proyect1.user.infra.in.kafka.mapper.AuthEventMapper;
import com.proyect1.user.application.kafka.input.AuthEvent;
import com.proyect1.user.application.usecase.AuthEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Adapter IN - Consumer de Kafka
 * Escucha eventos del Auth Service y reacciona
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthEventKafkaAdapter {

    private final ObjectMapper objectMapper;
    private final AuthEventService authEventService;

    @KafkaListener(
            topics = "${kafka.topics.auth-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleAuthEvent(String eventJson) throws JsonProcessingException {
        AuthEventDto dto = objectMapper.readValue(eventJson, AuthEventDto.class);
        AuthEvent event = AuthEventMapper.from(dto);
        authEventService.handle(event);
    }
}
