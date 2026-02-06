package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventResponse;
import com.decade.practice.dto.SeenEventResponse;
import com.decade.practice.persistence.jpa.entities.SeenEvent;
import org.springframework.stereotype.Component;

@Component

public class SeenEventConverter extends AbstractEventConverter<SeenEvent> {

    @Override
    protected EventResponse postInitEventResponse(SeenEvent event, EventResponse res) {
        return new EventResponse(
                res.id(),
                res.idempotencyKey(),
                res.sender(),
                res.textEvent(),
                res.imageEvent(),
                res.iconEvent(),
                res.preferenceEvent(),
                res.fileEvent(),
                new SeenEventResponse(event.getAt()),
                res.createdTime(),
                res.eventType(),
                res.eventVersion(),
                res.message(),
                res.owner(),
                res.partner(),
                res.chat()
        );
    }

    @Override
    public Class<SeenEvent> supports() {
        return SeenEvent.class;
    }
}
