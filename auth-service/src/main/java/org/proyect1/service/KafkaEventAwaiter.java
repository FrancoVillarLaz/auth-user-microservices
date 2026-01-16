package org.proyect1.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.proyect1.dto.event.AuthEvent;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class KafkaEventAwaiter {

    private final Map<String, CompletableFuture<AuthEvent>> pending =
            new ConcurrentHashMap<>();

    public CompletableFuture<AuthEvent> await(String eventId) {
        CompletableFuture<AuthEvent> future = new CompletableFuture<>();
        pending.put(eventId, future);
        return future;
    }

    public void complete(AuthEvent event) {
        CompletableFuture<AuthEvent> future =
                pending.remove(event.getEventId());

        if (future != null) {
            future.complete(event);
        }
    }

    public void fail(String eventId, Throwable error) {
        CompletableFuture<AuthEvent> future =
                pending.remove(eventId);

        if (future != null) {
            future.completeExceptionally(error);
        }
    }
}