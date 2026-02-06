package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventResponse;
import com.decade.practice.dto.IconEventResponse;
import com.decade.practice.persistence.jpa.entities.IconEvent;
import org.springframework.stereotype.Component;

@Component

public class IconEventConverter extends AbstractEventConverter<IconEvent> {

    @Override
    protected EventResponse postInitEventResponse(IconEvent event, EventResponse res) {
        return new EventResponse(
                res.id(),
                res.idempotencyKey(),
                res.sender(),
                res.textEvent(),
                res.imageEvent(),
                new IconEventResponse(event.getIconId()),
                res.preferenceEvent(),
                res.fileEvent(),
                res.seenEvent(),
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
    public Class<IconEvent> supports() {
        return IconEvent.class;
    }
}
