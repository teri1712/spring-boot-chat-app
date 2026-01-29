package com.decade.practice.application.usecases;

import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.persistence.jpa.entities.ChatEvent;

import java.util.UUID;

public interface EventStore extends EventService {
    void save(UUID senderId, UUID ownerId, ChatEvent event);

    boolean isAllowed(ChatIdentifier chatIdentifier, UUID userId);

}