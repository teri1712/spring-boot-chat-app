package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventCreateCommand;
import com.decade.practice.persistence.jpa.entities.ChatEvent;
import com.decade.practice.persistence.jpa.repositories.ChatRepository;
import com.decade.practice.persistence.jpa.repositories.UserRepository;

import java.util.UUID;

public abstract class EventFactory<E extends ChatEvent> {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    protected EventFactory(UserRepository userRepository, ChatRepository chatRepository) {
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
    }

    public E newInstance(EventCreateCommand command, UUID ownerId, UUID idempotencyKey) {
        E event = newInstance(command);
        event.setOwner(userRepository.findById(ownerId).orElseThrow());
        event.setSender(userRepository.findById(command.getSenderId()).orElseThrow());
        event.setChat(chatRepository.findById(command.getChatId()).orElseThrow());
        event.setIdempotentKey(idempotencyKey);
        return event;
    }

    protected abstract E newInstance(EventCreateCommand command);

    protected abstract boolean supports(EventCreateCommand command);
}
