package com.decade.practice.threads.application.events;

import com.decade.practice.engagement.api.events.ChatSnapshot;
import com.decade.practice.engagement.api.events.ImageEventPlaced;
import com.decade.practice.threads.application.ports.out.EventRepository;
import com.decade.practice.threads.domain.ImageEvent;
import com.decade.practice.threads.domain.ImageSpec;
import com.decade.practice.threads.domain.MessageEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ImageEventPlacedListener extends AbstractEventPlacedListener<ImageEventPlaced> {

    public ImageEventPlacedListener(EventRepository events) {
        super(events);
    }

    @Override
    @ApplicationModuleListener
    public void on(ImageEventPlaced eventPlaced) {
        super.on(eventPlaced);
    }

    @Override
    protected MessageEvent newInstance(ImageEventPlaced eventPlaced, ChatSnapshot snapshot, UUID ownerId) {
        return new ImageEvent(eventPlaced.getSenderId(), ownerId, snapshot.chatId(), snapshot.roomName(), snapshot.roomAvatar(),
                new ImageSpec(eventPlaced.getUri(), eventPlaced.getFilename(), eventPlaced.getWidth(), eventPlaced.getHeight(), eventPlaced.getFormat())
        );
    }
}
