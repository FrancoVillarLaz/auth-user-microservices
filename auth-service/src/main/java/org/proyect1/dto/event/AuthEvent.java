package org.proyect1.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.time.LocalDateTime;

@RegisterForReflection
public class AuthEvent {
    @JsonProperty("event_id")
    private String eventId;
    @JsonProperty("event_type")
    private EventType eventType;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("username")
    private String username;
    @JsonProperty("email")
    private String email;
    @JsonProperty("suscription_id")
    private String suscriptionId;
    @JsonProperty("user_metadata")
    private JsonNode userMetaData;
    @JsonProperty("ip_address")
    private String ipAddress;
    @JsonProperty("user_agent")
    private String userAgent;
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
    @JsonProperty("metadata")
    private String metadata;

    public AuthEvent() {
    }

    public AuthEvent(EventType eventType, Long userId, String username, String email) {
        this();
        this.eventType = eventType;
        this.userId = userId;
        this.username = username;
        this.email = email;
    }

    // Getters y setters
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    public JsonNode getUserMetaData() { return userMetaData; }
    public void setUserMetaData(JsonNode userMetaData) { this.userMetaData = userMetaData; }
    public String getSuscriptionId() { return suscriptionId; }
    public void setSuscriptionId(String suscriptionId) { this.suscriptionId = suscriptionId; }


}
