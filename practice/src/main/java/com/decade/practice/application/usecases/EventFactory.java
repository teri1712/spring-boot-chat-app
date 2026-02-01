package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventDto;
import com.decade.practice.dto.EventRequest;
import com.decade.practice.persistence.jpa.entities.ChatEvent;

public interface EventFactory<E extends ChatEvent> {

    Class<E> getSupportedType();

    default boolean support(ChatEvent event) {
        return getSupportedType().isInstance(event);
    }

    E createEvent(EventRequest eventRequest);

    EventDto createEventDto(ChatEvent event);
}
