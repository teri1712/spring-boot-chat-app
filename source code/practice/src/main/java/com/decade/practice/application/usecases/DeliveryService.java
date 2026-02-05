package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventDto;
import com.decade.practice.dto.EventRequest;
import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;

import java.util.UUID;

public interface DeliveryService {
    EventDto createAndSend(UUID senderId, ChatIdentifier chatIdentifier, UUID idempotentKey, EventRequest eventRequest);
}
