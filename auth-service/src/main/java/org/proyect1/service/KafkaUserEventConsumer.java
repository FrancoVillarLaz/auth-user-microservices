package org.proyect1.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.proyect1.dto.event.AuthEvent;
import org.proyect1.dto.event.UserCreatedEvent;
import org.proyect1.dto.event.UserCreationFailedEvent;
import org.proyect1.entity.User;
import org.proyect1.entity.UserLastModification;
import org.jboss.logging.Logger;

@ApplicationScoped
public class KafkaUserEventConsumer {

    private static final Logger LOG =
            Logger.getLogger(KafkaUserEventConsumer.class);

    @Inject
    ObjectMapper objectMapper;

    @Inject
    KafkaEventAwaiter eventAwaiter;

    @Incoming("user-events")
    @Blocking
    @Transactional
    public void consume(String message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            String eventType = jsonNode.get("event_type").asText();

            LOG.infov("[USER EVENT] eventId={0}, type={1}",
                    jsonNode.get("event_id").asText(),
                    eventType);

            AuthEvent event;

            switch(eventType) {
                case "USER_CREATION_SUCCESS":
                    event = objectMapper.readValue(message, UserCreatedEvent.class);
                    LOG.infov("Usuario creado exitosamente: userId={0}, type={1}",
                            event.getUserId(),
                            ((UserCreatedEvent) event).getUserType());
                    break;

                case "USER_CREATION_FAILED":
                    event = objectMapper.readValue(message, UserCreationFailedEvent.class);
                    LOG.warnv("Falló creación de usuario: userId={0}, reason={1}",
                            event.getUserId(),
                            ((UserCreationFailedEvent) event).getReason());

                    User user = new User();
                    user = user.findById(event.getUserId());

                    user.isActive=false;
                    user.lastModification= UserLastModification.ROLLBACK_USER_CREATION;
                    user.persist();
                    LOG.warnv("Usuario desactivado por fallo en creación: userId={0}",
                            event.getUserId());
                    
                    break;

                default:
                    event = objectMapper.readValue(message, AuthEvent.class);
                    LOG.infov("Otro tipo de evento: {0}", eventType);
            }

            eventAwaiter.complete(event);

        } catch (Exception e) {
            LOG.error("Error procesando evento Kafka", e);
        }
    }
}