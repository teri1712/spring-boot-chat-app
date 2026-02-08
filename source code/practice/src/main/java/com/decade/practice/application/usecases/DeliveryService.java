package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventCreateCommand;
import com.decade.practice.dto.EventDetails;

import java.util.UUID;

public interface DeliveryService {
    EventDetails createAndSend(UUID senderId, String chatId, UUID idempotentKey, EventCreateCommand command);
}
