package com.decade.practice.application.usecases;

import com.decade.practice.api.dto.ChatResponse;
import com.decade.practice.api.dto.EventDto;
import com.decade.practice.api.dto.UserResponse;
import com.decade.practice.persistence.jpa.entities.ChatEvent;
import com.decade.practice.persistence.jpa.entities.MessageEvent;
import com.decade.practice.utils.ChatUtils;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

public abstract class AbstractEventFactory<E extends ChatEvent> implements EventFactory<E> {


    abstract protected EventDto postInitEventResponse(E event, EventDto res);

    @Override
    public EventDto createEventDto(ChatEvent chatEvent) {

        if (chatEvent instanceof HibernateProxy proxy) {
            chatEvent = (ChatEvent) Hibernate.unproxy(proxy);
        }

        if (!support(chatEvent))
            throw new IllegalStateException("Chat event is not supported by this factory");

        EventDto event = new EventDto();
        event.setId(chatEvent.getId());
        event.setIdempotencyKey(chatEvent.getIdempotentKey());
        event.setEventVersion(chatEvent.getEventVersion());
        event.setEventType(chatEvent.getEventType());
        event.setMessage(chatEvent instanceof MessageEvent);
        event.setOwner(UserResponse.from(chatEvent.getOwner()));
        event.setPartner(UserResponse.from(ChatUtils.inspectPartner(chatEvent.getChat(), chatEvent.getOwner())));
        event.setChat(new ChatResponse(chatEvent.getChat(), chatEvent.getOwner()));
        event.setSender(chatEvent.getSender().getId());
        event.setCreatedTime(chatEvent.getCreatedTime());
        return postInitEventResponse((E) chatEvent, event);
    }
}
