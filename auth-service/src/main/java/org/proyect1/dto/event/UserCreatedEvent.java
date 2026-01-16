package org.proyect1.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class UserCreatedEvent extends AuthEvent {

    @JsonProperty("user_type")
    private String userType;

    @JsonProperty("created_entity")
    private String createdEntity;

    public UserCreatedEvent() {
        super();
    }

    public UserCreatedEvent(String eventId, EventType eventType, Long userId,
                            String username, String email, String userType,
                            String createdEntity) {
        super(eventType, userId, username, email);
        this.setEventId(eventId);
        this.setEventType(EventType.USER_CREATION_SUCCESS);
        this.userType = userType;
        this.createdEntity = createdEntity;
    }

    // Getters y setters
    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getCreatedEntity() {
        return createdEntity;
    }

    public void setCreatedEntity(String createdEntity) {
        this.createdEntity = createdEntity;
    }
}