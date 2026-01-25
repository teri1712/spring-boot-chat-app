package com.decade.practice.application.usecases;

import com.decade.practice.api.dto.EventDto;
import com.decade.practice.api.dto.EventRequest;
import com.decade.practice.api.dto.SeenEventDto;
import com.decade.practice.persistence.jpa.entities.SeenEvent;
import org.springframework.stereotype.Component;

@Component
public class SeenEventFactory extends AbstractEventFactory<SeenEvent> {

    @Override
    public Class<SeenEvent> getSupportedType() {
        return SeenEvent.class;
    }

    @Override
    public SeenEvent createEvent(EventRequest eventRequest) {
        SeenEvent seenEvent = new SeenEvent();
        seenEvent.setChatIdentifier(eventRequest.getChatIdentifier());
        seenEvent.setAt(eventRequest.getSeenEvent().getAt());
        seenEvent.setEventType("SEEN");
        return seenEvent;
    }

    @Override
    protected EventDto postInitEventResponse(SeenEvent event, EventDto res) {
        res.setSeenEvent(new SeenEventDto(event.getAt()));
        return res;
    }
}
