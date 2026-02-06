package com.decade.practice.application.services;

import com.decade.practice.application.usecases.ChatService;
import com.decade.practice.application.usecases.EventStore;
import com.decade.practice.dto.EventDetails;
import com.decade.practice.dto.EventRequest;
import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.utils.ChatUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class ChatEventStore {
    private final EventStore eventStore;
    private final ChatService chatService;


    @Transactional
    public List<EventDetails> save(UUID senderId, UUID ownerId, UUID idempotentKey, ChatIdentifier chatIdentifier, EventRequest eventRequest) {
        UUID receiverId = ChatUtils.inspectPartner(chatIdentifier, senderId);
        chatService.ensureExist(chatIdentifier);

        List<EventDetails> mineStored = eventStore.save(senderId, ownerId, idempotentKey, chatIdentifier, eventRequest);
        List<EventDetails> yoursStored = eventStore.save(senderId, receiverId, UUID.randomUUID(), chatIdentifier, eventRequest);


        return Stream.of(mineStored, yoursStored).flatMap(List::stream).toList();
    }
}
