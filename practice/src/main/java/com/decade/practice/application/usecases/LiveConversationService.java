package com.decade.practice.application.usecases;

import com.decade.practice.api.dto.TypeEventDto;
import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import org.springframework.messaging.Message;

import java.util.UUID;

public interface LiveConversationService {
    void send(ChatIdentifier identifier, TypeEventDto event);

    void subscribe(ChatIdentifier identifier, UUID userId, Message<?> message);
}
