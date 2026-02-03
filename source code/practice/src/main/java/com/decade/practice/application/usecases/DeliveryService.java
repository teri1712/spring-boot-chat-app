package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventDto;
import com.decade.practice.dto.EventRequest;
import com.decade.practice.persistence.jpa.entities.ChatEvent;

import java.util.UUID;

public interface DeliveryService {
    <E extends ChatEvent> EventDto createAndSend(UUID idempotentKey, EventRequest eventRequest, EventFactory<E> eventFactory);
}
