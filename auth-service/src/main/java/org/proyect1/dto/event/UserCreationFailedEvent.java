package org.proyect1.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class UserCreationFailedEvent extends AuthEvent {

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("user_type")
    private String userType;

    public UserCreationFailedEvent() {
        super();
    }

    public UserCreationFailedEvent(String eventId, EventType eventType, Long userId,
                                   String username, String email, String userType,
                                   String reason) {
        super(eventType, userId, username, email);
        this.setEventId(eventId);
        this.setEventType(EventType.USER_CREATION_FAILED);
        this.userType = userType;
        this.reason = reason;
    }

    // Getters y setters
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}