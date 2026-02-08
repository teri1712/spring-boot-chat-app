package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventCreateCommand;
import com.decade.practice.persistence.jpa.entities.ChatEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EventFactoryResolution {

    private final List<EventFactory<? extends ChatEvent>> eventFactory;

    public Optional<ChatEvent> newInstance(EventCreateCommand eventCommand, UUID ownerId, UUID idempotencyKey) {
        for (EventFactory<? extends ChatEvent> factory : eventFactory) {
            if (factory.supports(eventCommand)) {
                return Optional.of(factory.newInstance(eventCommand, ownerId, idempotencyKey));
            }
        }
        return Optional.empty();
    }


}
