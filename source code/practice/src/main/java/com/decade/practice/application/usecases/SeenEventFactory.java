package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventRequest;
import com.decade.practice.persistence.jpa.entities.SeenEvent;
import org.springframework.stereotype.Component;

@Component

public class SeenEventFactory implements EventFactory<SeenEvent> {

    @Override
    public SeenEvent newInstance(EventRequest eventRequest) {
        SeenEvent seenEvent = new SeenEvent();
        seenEvent.setAt(eventRequest.getSeenEvent().getAt());
        seenEvent.setEventType("SEEN");
        return seenEvent;
    }

    @Override
    public boolean supports(EventRequest eventRequest) {
        return eventRequest.getSeenEvent() != null;
    }
}
