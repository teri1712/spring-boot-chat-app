package com.decade.practice.application.services;

import com.decade.practice.application.usecases.EventStore;
import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.persistence.jpa.entities.*;
import com.decade.practice.persistence.jpa.repositories.ChatOrderRepository;
import com.decade.practice.persistence.jpa.repositories.EventRepository;
import com.decade.practice.persistence.jpa.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.function.Supplier;

@Component("eventStore")
@AllArgsConstructor
public class UserEventStore implements EventStore {

    private final ChatOrderRepository chatOrderRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public boolean isAllowed(ChatIdentifier chatIdentifier, UUID userId) {
        return chatIdentifier.getFirstUser().equals(userId) || chatIdentifier.getSecondUser().equals(userId);
    }

    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED
    )
    @Override
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
}