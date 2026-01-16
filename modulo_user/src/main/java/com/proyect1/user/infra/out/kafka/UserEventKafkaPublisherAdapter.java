package com.proyect1.user.infra.out.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyect1.user.application.kafka.events.UserCreatedEvent;
import com.proyect1.user.application.kafka.events.UserCreationFailedEvent;
import com.proyect1.user.ports.out.UserEventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventKafkaPublisherAdapter implements UserEventPublisherPort {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "user-events";

    @Override
    public void publishUserCreated(UserCreatedEvent event) {
        sendEvent(event.eventId(), event);
    }

    @Override
    public void publishUserCreationFailed(UserCreationFailedEvent event) {
        sendEvent(event.eventId(), event);
    }

    private void sendEvent(String key, Object event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, key, json);

            log.info("[KAFKA] Sent event to {} with key={}: {}",
                    TOPIC, key, json);

        } catch (Exception e) {
            log.error("Error publishing event to kafka: {}", e.getMessage(), e);
        }
    }
}
