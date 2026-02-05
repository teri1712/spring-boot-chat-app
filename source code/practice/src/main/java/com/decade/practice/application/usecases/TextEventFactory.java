package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventRequest;
import com.decade.practice.persistence.jpa.entities.TextEvent;
import org.springframework.stereotype.Component;

@Component
public class TextEventFactory implements EventFactory<TextEvent> {


    @Override
    public TextEvent newInstance(EventRequest eventRequest) {
        TextEvent textEvent = new TextEvent();
        textEvent.setContent(eventRequest.getTextEvent().getContent());
        textEvent.setEventType("TEXT");
        return textEvent;
    }

    @Override
    public boolean supports(EventRequest eventRequest) {
        return eventRequest.getTextEvent() != null;
    }
}
