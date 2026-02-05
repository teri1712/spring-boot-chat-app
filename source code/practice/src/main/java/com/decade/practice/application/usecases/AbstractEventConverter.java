package com.decade.practice.application.usecases;

import com.decade.practice.dto.ChatDto;
import com.decade.practice.dto.EventDto;
import com.decade.practice.dto.UserResponse;
import com.decade.practice.persistence.jpa.entities.ChatEvent;
import com.decade.practice.persistence.jpa.entities.MessageEvent;
import com.decade.practice.utils.ChatUtils;
import jakarta.validation.constraints.NotNull;

public abstract class AbstractEventConverter<E extends ChatEvent> extends EventConverter<E> {


    @NotNull
    abstract protected EventDto postInitEventResponse(E event, EventDto res);

    @Override
    public EventDto doConvert(E chatEvent) {

        EventDto event = new EventDto();
        event.setId(chatEvent.getId());
        event.setIdempotencyKey(chatEvent.getIdempotentKey());
        event.setEventVersion(chatEvent.getEventVersion());
        event.setEventType(chatEvent.getEventType());
        event.setMessage(chatEvent instanceof MessageEvent);
        event.setOwner(UserResponse.from(chatEvent.getOwner()));
        event.setPartner(UserResponse.from(ChatUtils.inspectPartner(chatEvent.getChat(), chatEvent.getOwner())));
        event.setChat(new ChatDto(chatEvent.getChat(), chatEvent.getOwner()));
        event.setSender(chatEvent.getSender().getId());
        event.setCreatedTime(chatEvent.getCreatedTime());
        return postInitEventResponse(chatEvent, event);
    }
}
