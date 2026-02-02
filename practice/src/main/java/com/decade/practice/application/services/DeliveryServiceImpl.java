package com.decade.practice.application.services;

import com.decade.practice.application.usecases.*;
import com.decade.practice.dto.EventDto;
import com.decade.practice.dto.EventRequest;
import com.decade.practice.dto.events.MessageCreatedEvent;
import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.persistence.jpa.entities.Chat;
import com.decade.practice.persistence.jpa.entities.ChatEvent;
import com.decade.practice.persistence.jpa.repositories.ChatRepository;
import com.decade.practice.persistence.jpa.repositories.EventRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryServiceImpl implements DeliveryService {


    @PersistenceContext
    private EntityManager em;

    private final EventStore eventStore;
    private final EventSender eventSender;
    private final ChatService chatService;
    private final ChatRepository chatRepo;
    private final EventRepository eventRepo;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public <E extends ChatEvent> EventDto createAndSend(UUID idempotentKey, EventRequest eventRequest, EventFactory<E> eventFactory) {

        ChatIdentifier chatIdentifier = eventRequest.getChatIdentifier();
        UUID receiverId = chatIdentifier.getSecondUser().equals(eventRequest.getSender()) ? chatIdentifier.getFirstUser() : chatIdentifier.getSecondUser();
        UUID senderId = eventRequest.getSender();


        E mine = eventFactory.createEvent(eventRequest);
        E yours = eventFactory.createEvent(eventRequest);
        mine.setIdempotentKey(idempotentKey);
        try {
            Chat chat = chatRepo.findByIdWithPessimisticLock(mine.getChatIdentifier());
            if (chat == null) {
                chatService.createChat(mine.getChatIdentifier());
                chat = chatRepo.findByIdWithPessimisticLock(mine.getChatIdentifier());
            }
            chat.setMessageCount(chat.getMessageCount() + 1);
            mine.setChat(chat);
            yours.setChat(chat);

            eventStore.save(senderId, senderId, mine);
            eventStore.save(senderId, receiverId, yours);

            em.flush();

            EventDto myEventDto = eventFactory.createEventDto(mine);
            EventDto yourDto = eventFactory.createEventDto(yours);

            eventSender.send(myEventDto);
            eventSender.send(yourDto);


            eventPublisher.publishEvent(myEventDto);
            eventPublisher.publishEvent(yourDto);

            MessageCreatedEvent myCreatedEvent = MessageCreatedEvent.from(myEventDto);
            if (myCreatedEvent != null) {
                eventPublisher.publishEvent(myCreatedEvent);
            }
            MessageCreatedEvent yourCreatedEvent = MessageCreatedEvent.from(yourDto);
            if (yourCreatedEvent != null) {
                eventPublisher.publishEvent(yourCreatedEvent);
            }


        } catch (ConstraintViolationException e) {
            log.debug("Event already sent", e);
            ChatEvent persistedOne = eventRepo.findByIdempotentKey(mine.getIdempotentKey()).orElseThrow();
            mine = (E) persistedOne;
        }
        return eventFactory.createEventDto(mine);
    }
}
