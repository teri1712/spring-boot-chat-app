package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventDto;
import com.decade.practice.dto.TextEventDto;
import com.decade.practice.persistence.jpa.entities.TextEvent;
import org.springframework.stereotype.Component;

@Component

public class TextEventConverter extends AbstractEventConverter<TextEvent> {
    @Override
    protected EventDto postInitEventResponse(TextEvent event, EventDto res) {
        res.setTextEvent(new TextEventDto(event.getContent()));
        return res;
    }

    @Override
    public Class<TextEvent> supports() {
        return TextEvent.class;
    }
}
