package com.decade.practice.application.usecases;

import com.decade.practice.dto.TypeEventDto;
import com.decade.practice.persistence.jpa.embeddables.ChatCreators;
import org.springframework.messaging.Message;

import java.util.UUID;

public interface LiveService {
    void send(ChatCreators identifier, TypeEventDto event);

    void subscribe(ChatCreators identifier, UUID userId, Message<?> message);
}
