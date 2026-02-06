package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventResponse;
import com.decade.practice.dto.PreferenceEventResponse;
import com.decade.practice.dto.PreferenceResponse;
import com.decade.practice.persistence.jpa.entities.PreferenceEvent;
import org.springframework.stereotype.Component;

@Component
public class PreferenceEventConverter extends AbstractEventConverter<PreferenceEvent> {

    @Override
    protected EventResponse postInitEventResponse(PreferenceEvent chatEvent, EventResponse res) {
        return new EventResponse(
                res.id(),
                res.idempotencyKey(),
                res.sender(),
                res.textEvent(),
                res.imageEvent(),
                res.iconEvent(),
                new PreferenceEventResponse(PreferenceResponse.from(chatEvent.getPreference())),
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
    public Class<PreferenceEvent> supports() {
        return PreferenceEvent.class;
    }
}
