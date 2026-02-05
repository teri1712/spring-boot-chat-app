package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventDto;
import com.decade.practice.dto.IconEventDto;
import com.decade.practice.persistence.jpa.entities.IconEvent;
import org.springframework.stereotype.Component;

@Component

public class IconEventConverter extends AbstractEventConverter<IconEvent> {

    @Override
    protected EventDto postInitEventResponse(IconEvent event, EventDto res) {
        res.setIconEvent(new IconEventDto(event.getIconId()));
        return res;
    }

    @Override
    public Class<IconEvent> supports() {
        return IconEvent.class;
    }
}
