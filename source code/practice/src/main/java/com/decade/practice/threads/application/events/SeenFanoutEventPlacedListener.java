package com.decade.practice.threads.application.events;

import com.decade.practice.engagement.api.events.SeenEventPlaced;
import com.decade.practice.threads.application.ports.out.EventRepository;
import com.decade.practice.threads.domain.ChatEvent;
import com.decade.practice.threads.domain.SeenEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SeenFanoutEventPlacedListener extends AbstractFanoutEventPlacedListener<SeenEventPlaced> {

    public SeenFanoutEventPlacedListener(EventRepository events) {
        super(events);
    }

    @Override
    @ApplicationModuleListener
    public void on(SeenEventPlaced eventPlaced) {
        super.on(eventPlaced);
    }

    @Override
    protected ChatEvent newInstance(SeenEventPlaced eventPlaced, UUID ownerId) {
        return new SeenEvent(eventPlaced.getSenderId(), ownerId, eventPlaced.getSnapshot().chatId(), eventPlaced.getAt());
    }
}
