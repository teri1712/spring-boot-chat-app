package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventDetails;
import com.decade.practice.dto.EventRequest;
import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventStore extends EventService {
    List<EventDetails> save(UUID senderId, UUID ownerId, UUID idempotentKey, ChatIdentifier chatIdentifier, EventRequest eventRequest);
}