package com.decade.practice.application.services;

import com.decade.practice.application.usecases.*;
import com.decade.practice.dto.Conversation;
import com.decade.practice.dto.EventCreateCommand;
import com.decade.practice.dto.EventDetails;
import com.decade.practice.dto.EventResponse;
import com.decade.practice.dto.mapper.ConversationMapper;
import com.decade.practice.persistence.jpa.entities.Chat;
import com.decade.practice.persistence.jpa.entities.ChatEvent;
import com.decade.practice.persistence.jpa.repositories.ChatRepository;
import com.decade.practice.persistence.jpa.repositories.EventRepository;
import com.decade.practice.persistence.jpa.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeliveryServiceImpl implements DeliveryService {


    private final EventStore eventStore;
    private final EventSender eventSender;
    private final EventRepository eventRepo;
    private final EventConverterResolution converterResolution;
    private final ConversationMapper conversationMapper;
    private final EventFactoryResolution factoryResolution;
    private final ChatRepository chatRepo;
    private final UserRepository userRepository;


    @Override
    @PreAuthorize("@accessPolicy.isAllowed(#chatId,#senderId)")
    public EventDetails createAndSend(UUID senderId, String chatId, UUID idempotentKey, EventCreateCommand command) {
        try {

            Chat chat = chatRepo.findById(chatId).orElseThrow();

            chat.getParticipants().forEach(participant -> {
                UUID key = participant.getId().equals(senderId) ? idempotentKey : UUID.randomUUID();
                ChatEvent event = factoryResolution.newInstance(command, participant.getId(), key).orElseThrow();
                EventDetails eventDetails = eventStore.save(event);
                eventSender.send(eventDetails);
            });

        } catch (DataIntegrityViolationException e) {
            log.debug("Event already sent", e);
        }
        ChatEvent existingOne = eventRepo.findByIdempotentKey(idempotentKey).orElseThrow();
        EventResponse response = converterResolution.convert(existingOne);
        Conversation conversation = conversationMapper.toConversation(existingOne.getChat(), existingOne.getOwner());
        return new EventDetails(response, conversation);
    }
}
