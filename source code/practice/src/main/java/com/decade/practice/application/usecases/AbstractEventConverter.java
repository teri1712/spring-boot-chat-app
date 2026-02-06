package com.decade.practice.application.usecases;

import com.decade.practice.dto.ChatResponse;
import com.decade.practice.dto.EventResponse;
import com.decade.practice.persistence.jpa.entities.ChatEvent;
import com.decade.practice.persistence.jpa.entities.MessageEvent;
import com.decade.practice.utils.ChatUtils;
import jakarta.validation.constraints.NotNull;

public abstract class AbstractEventConverter<E extends ChatEvent> extends EventConverter<E> {


    @NotNull
    abstract protected EventResponse postInitEventResponse(E event, EventResponse res);

    @Override
    public EventResponse doConvert(E chatEvent) {

        EventResponse event = new EventResponse(
                chatEvent.getId(),
                chatEvent.getIdempotentKey(),
                chatEvent.getSender().getId(),
                null, // textEvent
                null, // imageEvent
                null, // iconEvent
                null, // preferenceEvent
                null, // fileEvent
                null, // seenEvent
                chatEvent.getCreatedTime(),
                chatEvent.getEventType(),
                chatEvent.getEventVersion(),
                chatEvent instanceof MessageEvent,
                chatEvent.getOwner().getId(),
                ChatUtils.inspectPartner(chatEvent.getChat(), chatEvent.getOwner()).getId(),
                ChatResponse.from(chatEvent.getChat(), chatEvent.getOwner())
        );
        return postInitEventResponse(chatEvent, event);
    }
}
