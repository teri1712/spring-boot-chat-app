package com.decade.practice.application.services;

import com.decade.practice.application.domain.ChatThread;
import com.decade.practice.application.domain.MessagePolicy;
import com.decade.practice.application.domain.UserThread;
import com.decade.practice.application.usecases.EventConverterResolution;
import com.decade.practice.application.usecases.EventService;
import com.decade.practice.application.usecases.EventStore;
import com.decade.practice.dto.Conversation;
import com.decade.practice.dto.EventDetails;
import com.decade.practice.dto.EventResponse;
import com.decade.practice.dto.events.MessageCreatedEvent;
import com.decade.practice.dto.mapper.ConversationMapper;
import com.decade.practice.persistence.jpa.entities.Chat;
import com.decade.practice.persistence.jpa.entities.ChatEvent;
import com.decade.practice.persistence.jpa.entities.ChatOrder;
import com.decade.practice.persistence.jpa.entities.User;
import com.decade.practice.persistence.jpa.repositories.ChatOrderRepository;
import com.decade.practice.persistence.jpa.repositories.ChatRepository;
import com.decade.practice.persistence.jpa.repositories.EventRepository;
import com.decade.practice.persistence.jpa.repositories.UserRepository;
import com.decade.practice.utils.EventUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
@Transactional
public class UserEventStore implements EventStore, EventService {

    private final ChatOrderRepository chatOrderRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    private final EventConverterResolution converterResolution;
    private final MessagePolicy messagePolicy;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ConversationMapper conversationMapper;
    private final ChatRepository chatRepository;
    private final UserThread userThread;
    private final ChatThread chatThread;

    @Override
    public EventDetails save(ChatEvent event) {

        UUID ownerId = event.getOwner().getId();
        Chat chat = event.getChat();

        User owner = userRepository.findByIdWithPessimisticWrite(ownerId).orElseThrow();
        ChatOrder order = chatOrderRepository.findByChatAndOwner(chat, owner)
                .orElseGet(() -> ChatOrder.of(chat, owner));
        userThread.registerEvent(event);
        chatThread.bubbleUp(order, event);
        
        EventResponse eventResponse = converterResolution.convert(event);
        Conversation conversation = conversationMapper.toConversation(chat, owner);
        EventDetails eventDetails = new EventDetails(eventResponse, conversation);
        MessageCreatedEvent messageCreatedEvent = MessageCreatedEvent.from(eventDetails);
        if (messageCreatedEvent != null) {
            applicationEventPublisher.publishEvent(messageCreatedEvent);
        }
        return eventDetails;
    }


    @Override
    @PreAuthorize("@accessPolicy.isAllowed(#chatId,#userId)")
    public List<EventResponse> findByOwnerAndChatAndEventVersionLessThanEqual(UUID userId, String chatId, int eventVersion) {

        log.trace("Finding events for owner '{}' and chat '{}'", userId, chatId);
        return eventRepository.findByOwner_IdAndChat_IdentifierAndEventVersionLessThanEqual(userId, chatId, eventVersion, EventUtils.EVENT_VERSION_LESS_THAN_EQUAL)
                .stream().map(converterResolution::convert).toList();
    }

    @Override
    public List<EventResponse> findByOwnerAndEventVersionLessThanEqual(UUID userId, int eventVersion) {
        log.trace("Finding events for owner '{}'", userId);
        return eventRepository.findByOwner_IdAndEventVersionLessThanEqual(userId, eventVersion, EventUtils.EVENT_VERSION_LESS_THAN_EQUAL)
                .stream().map(converterResolution::convert).toList();
    }

    @Override
    public EventDetails findFirstByOwnerOrderByEventVersionDesc(UUID userId) {
        return eventRepository.findFirstByOwner_IdOrderByEventVersionDesc(userId).map(chatEvent -> {
            Conversation conversation = conversationMapper.toConversation(chatEvent.getChat(), chatEvent.getOwner());
            return new EventDetails(converterResolution.convert(chatEvent), conversation);
        }).orElse(null);
    }
}