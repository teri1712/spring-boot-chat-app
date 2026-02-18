package com.decade.practice.threads.application.events;

import com.decade.practice.engagement.api.events.IconEventPlaced;
import com.decade.practice.threads.application.ports.out.EventRepository;
import com.decade.practice.threads.domain.IconEvent;
import com.decade.practice.threads.domain.MessageEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class IconFanoutEventPlacedListener extends AbstractFanoutEventPlacedListener<IconEventPlaced> {


    public IconFanoutEventPlacedListener(EventRepository events) {
        super(events);
    }

    @Override
    @ApplicationModuleListener
    public void on(IconEventPlaced eventPlaced) {
        super.on(eventPlaced);
    }

    @Override
    protected MessageEvent newInstance(IconEventPlaced eventPlaced, UUID ownerId) {
        return new IconEvent(eventPlaced.getSenderId(), ownerId, eventPlaced.getSnapshot().chatId(), eventPlaced.getIconId());
    }
}
