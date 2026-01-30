package com.decade.practice.application.services;

import com.decade.practice.api.dto.EventDto;
import com.decade.practice.application.usecases.EventFactoryResolution;
import com.decade.practice.application.usecases.EventStore;
import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.persistence.jpa.entities.*;
import com.decade.practice.persistence.jpa.repositories.ChatOrderRepository;
import com.decade.practice.persistence.jpa.repositories.EventRepository;
import com.decade.practice.persistence.jpa.repositories.UserRepository;
import com.decade.practice.utils.EventUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
@Component("eventStore")
@AllArgsConstructor
public class UserEventStore implements EventStore {

    private final ChatOrderRepository chatOrderRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private EventFactoryResolution factoryResolution;

    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED
    )
    @Override
    @PreAuthorize("@accessPolicy.isAllowed(#event.chatIdentifier,#ownerId)")
    public void save(UUID senderId, UUID ownerId, ChatEvent event) {
        User owner = userRepository.findByIdWithPessimisticWrite(ownerId).orElseThrow();
        owner.getSyncContext().incVersion();
        User sender = userRepository.findById(senderId).orElseThrow();
        event.setSender(sender);
        event.setOwner(owner);

        Chat chat = event.getChat();

        SyncContext syncContext = owner.getSyncContext();
        event.setEventVersion(syncContext.getEventVersion());
        eventRepository.save(event);
        if (event instanceof MessageEvent) {
            ChatOrder order = chatOrderRepository.findByChatAndOwner(chat, owner).orElseGet(new Supplier<ChatOrder>() {
                @Override
                public ChatOrder get() {
                    ChatOrder chatOrder = new ChatOrder();
                    chatOrder.setChat(chat);
                    chatOrder.setOwner(owner);
                    return chatOrder;
                }
            });
            order.setCurrentVersion(event.getEventVersion());
            order.setCurrentEvent(event);

            chatOrderRepository.save(order);
        }
    }

    @Override
    @PreAuthorize("@accessPolicy.isAllowed(#chatIdentifier,#userId)")
    public List<EventDto> findByOwnerAndChatAndEventVersionLessThanEqual(UUID userId, ChatIdentifier chatIdentifier, int eventVersion) {
        log.trace("Finding events for owner '{}' and chat '{}'", userId, chatIdentifier);
        return eventRepository.findByOwner_IdAndChat_IdentifierAndEventVersionLessThanEqual(userId, chatIdentifier, eventVersion, EventUtils.EVENT_VERSION_LESS_THAN_EQUAL)
                .stream().map(new Function<ChatEvent, EventDto>() {
                    @Override
                    public EventDto apply(ChatEvent chatEvent) {
                        return factoryResolution.mapToDto(chatEvent);
                    }
                }).toList();
    }

    @Override
    public List<EventDto> findByOwnerAndEventVersionLessThanEqual(UUID userId, int eventVersion) {
        log.trace("Finding events for owner '{}'", userId);
        return eventRepository.findByOwner_IdAndEventVersionLessThanEqual(userId, eventVersion, EventUtils.EVENT_VERSION_LESS_THAN_EQUAL)
                .stream().map(new Function<ChatEvent, EventDto>() {
                    @Override
                    public EventDto apply(ChatEvent chatEvent) {
                        return factoryResolution.mapToDto(chatEvent);
                    }
                }).toList()
                ;
    }

    @Override
    public EventDto findFirstByOwnerOrderByEventVersionDesc(UUID userId) {
        return eventRepository.findFirstByOwner_IdOrderByEventVersionDesc(userId).map(new Function<ChatEvent, EventDto>() {
            @Override
            public EventDto apply(ChatEvent chatEvent) {
                return factoryResolution.mapToDto(chatEvent);
            }
        }).orElse(null);
    }
}