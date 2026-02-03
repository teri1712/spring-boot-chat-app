package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventDto;
import com.decade.practice.dto.EventRequest;
import com.decade.practice.dto.IconEventDto;
import com.decade.practice.persistence.jpa.entities.IconEvent;
import org.springframework.stereotype.Component;

@Component
public class IconEventFactory extends AbstractEventFactory<IconEvent> {

    @Override
    protected EventDto postInitEventResponse(IconEvent event, EventDto res) {
        res.setIconEvent(new IconEventDto(event.getIconId()));
        return res;
    }

    @Override
    public Class<IconEvent> getSupportedType() {
        return IconEvent.class;
    }

    @Override
    public IconEvent createEvent(EventRequest eventRequest) {
        IconEvent iconEvent = new IconEvent();
        iconEvent.setChatIdentifier(eventRequest.getChatIdentifier());
        iconEvent.setIconId(eventRequest.getIconEvent().getIconId());
        iconEvent.setEventType("ICON");
        return iconEvent;
    }
}
