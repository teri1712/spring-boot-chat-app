package com.decade.practice.threads.application.events;

import com.decade.practice.engagement.api.events.ChatSnapshot;
import com.decade.practice.engagement.api.events.TextEventPlaced;
import com.decade.practice.threads.application.ports.out.EventRepository;
import com.decade.practice.threads.domain.MessageEvent;
import com.decade.practice.threads.domain.TextEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TextEventPlacedListener extends AbstractEventPlacedListener<TextEventPlaced> {


    public TextEventPlacedListener(EventRepository events) {
        super(events);
    }

    @Override
    @ApplicationModuleListener
    public void on(TextEventPlaced eventPlaced) {
        super.on(eventPlaced);
    }

    @Override
    protected MessageEvent newInstance(TextEventPlaced eventPlaced, ChatSnapshot snapshot, UUID ownerId) {
        return new TextEvent(eventPlaced.getSenderId(), ownerId, snapshot.chatId(), snapshot.roomName(), snapshot.roomAvatar(), eventPlaced.getContent());
    }
}
