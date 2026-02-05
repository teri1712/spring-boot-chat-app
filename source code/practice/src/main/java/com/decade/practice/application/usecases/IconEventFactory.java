package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventRequest;
import com.decade.practice.persistence.jpa.entities.IconEvent;
import org.springframework.stereotype.Component;

@Component

public class IconEventFactory implements EventFactory<IconEvent> {
    @Override
    public IconEvent newInstance(EventRequest eventRequest) {
        IconEvent iconEvent = new IconEvent();
        iconEvent.setIconId(eventRequest.getIconEvent().getIconId());
        iconEvent.setEventType("ICON");
        return iconEvent;
    }

    @Override
    public boolean supports(EventRequest eventRequest) {
        return eventRequest.getIconEvent() != null;
    }
}
