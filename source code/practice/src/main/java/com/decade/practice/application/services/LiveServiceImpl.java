package com.decade.practice.application.services;

import com.decade.practice.application.usecases.EventSender;
import com.decade.practice.application.usecases.LiveService;
import com.decade.practice.dto.TypeEventDto;
import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.persistence.redis.TypeEvent;
import com.decade.practice.persistence.redis.repositories.TypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.decade.practice.api.websocket.ConversationController.resolveChatDestination;

@AllArgsConstructor
@Service
public class LiveServiceImpl implements LiveService {
    private final SimpMessagingTemplate brokerTemplate;
    private final EventSender eventSender;
    private final TypeRepository typeRepository;

    @Override
    @PreAuthorize("@accessPolicy.isAllowed(#identifier,#event.from)")
    public void send(ChatIdentifier identifier, TypeEventDto event) {
        TypeEvent typeEvent = new TypeEvent();
        typeEvent.setKey(event.getKey());
        typeEvent.setChat(identifier);
        typeEvent.setFrom(event.getFrom());
        typeEvent.setTime(event.getTime());
        typeRepository.save(typeEvent);

        eventSender.send(event);
    }

    @Override
    @PreAuthorize("@accessPolicy.isAllowed(#identifier,#userId)")
    public void subscribe(ChatIdentifier identifier, UUID userId, Message<?> message) {
        brokerTemplate.send(resolveChatDestination(identifier), message);
    }
}
