package org.proyect1.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import io.vertx.core.json.Json;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.proyect1.dto.event.AuthEvent;
import org.proyect1.dto.event.EventType;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;

@ApplicationScoped
public class KafkaEventProducer {

    private static final Logger LOG = Logger.getLogger(KafkaEventProducer.class);

    @Inject
    @Channel("auth-events")
    Emitter<String> authEventsEmitter;

    @Inject
    ObjectMapper objectMapper;

    public void publishAuthEvent(AuthEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            String key = event.getUserId() != null ? event.getUserId().toString() : event.getEventId();

            OutgoingKafkaRecordMetadata<String> metadata = OutgoingKafkaRecordMetadata.<String>builder()
                    .withKey(key)
                    .build();

            Message<String> message = Message.of(eventJson)
                    .addMetadata(metadata);

            authEventsEmitter.send(message);

            LOG.infof("Published auth event: %s for user: %s",
                    event.getEventType(), event.getUsername());

        } catch (JsonProcessingException e) {
            LOG.errorf("Error serializing auth event: %s", e.getMessage());
        } catch (Exception e) {
            LOG.errorf("Error publishing auth event: %s", e.getMessage());
        }
    }

    public void publishUserRegistered(
            String eventId,
            Long userId,
            String username,
            String email,
            String suscriptionId,
            String ip,
            JsonNode userMeta
    ) {

        AuthEvent event = new AuthEvent();
        event.setEventId(eventId);
        event.setEventType(EventType.valueOf("USER_REGISTERED"));
        event.setUserId(userId);
        event.setUsername(username);
        event.setEmail(email);
        event.setSuscriptionId(suscriptionId);
        event.setIpAddress(ip);
        event.setUserMetaData(userMeta);
        event.setTimestamp(LocalDateTime.now());

        authEventsEmitter.send(Json.encode(event));

    }


    public void publishUserLoggedIn(Long userId, String username, String email, String ipAddress, String userAgent) {
        AuthEvent event = new AuthEvent(EventType.USER_LOGGED_IN, userId, username, email);
        event.setIpAddress(ipAddress);
        event.setUserAgent(userAgent);
        publishAuthEvent(event);
    }

    public void publishUserLoggedOut(Long userId, String username, String ipAddress) {
        AuthEvent event = new AuthEvent(EventType.USER_LOGGED_OUT, userId, username, null);
        event.setIpAddress(ipAddress);
        publishAuthEvent(event);
    }

    public void publishTokenRefreshed(Long userId, String username) {
        AuthEvent event = new AuthEvent(EventType.TOKEN_REFRESHED, userId, username, null);
        publishAuthEvent(event);
    }

    public void publishLoginFailed(String identifier, String ipAddress, String reason) {
        AuthEvent event = new AuthEvent(EventType.LOGIN_FAILED, null, identifier, null);
        event.setIpAddress(ipAddress);
        event.setMetadata(reason);
        publishAuthEvent(event);
    }

    public void publishRegistrationFailed(String username, String email, String reason) {
        AuthEvent event = new AuthEvent(EventType.REGISTRATION_FAILED, null, username, email);
        event.setMetadata(reason);
        publishAuthEvent(event);
    }


}
