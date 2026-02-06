package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventDetails;
import com.decade.practice.dto.EventRequest;
import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;

import java.util.UUID;

public interface DeliveryService {
    EventDetails createAndSend(UUID senderId, ChatIdentifier chatIdentifier, UUID idempotentKey, EventRequest eventRequest);
}
