package com.decade.practice.application.usecases;

import com.decade.practice.api.dto.EventDto;
import com.decade.practice.api.dto.EventRequest;
import com.decade.practice.api.dto.TextEventDto;
import com.decade.practice.persistence.jpa.entities.TextEvent;
import org.springframework.stereotype.Component;

@Component
public class TextEventFactory extends AbstractEventFactory<TextEvent> {

    @Override
    public Class<TextEvent> getSupportedType() {
        return TextEvent.class;
    }

    @Override
    public TextEvent createEvent(EventRequest eventRequest) {
        TextEvent textEvent = new TextEvent();
        textEvent.setChatIdentifier(eventRequest.getChatIdentifier());
        textEvent.setContent(eventRequest.getTextEvent().getContent());
        textEvent.setEventType("TEXT");
        return textEvent;
    }

    @Override
    protected EventDto postInitEventResponse(TextEvent event, EventDto res) {
        res.setTextEvent(new TextEventDto(event.getContent()));
        return res;
    }

}
