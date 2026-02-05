package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventDto;
import com.decade.practice.dto.SeenEventDto;
import com.decade.practice.persistence.jpa.entities.SeenEvent;
import org.springframework.stereotype.Component;

@Component

public class SeenEventConverter extends AbstractEventConverter<SeenEvent> {

    @Override
    protected EventDto postInitEventResponse(SeenEvent event, EventDto res) {
        res.setSeenEvent(new SeenEventDto(event.getAt()));
        return res;
    }

    @Override
    public Class<SeenEvent> supports() {
        return SeenEvent.class;
    }
}
