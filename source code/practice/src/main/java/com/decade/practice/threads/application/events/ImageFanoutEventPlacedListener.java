package com.decade.practice.threads.application.events;

import com.decade.practice.engagement.api.events.ImageEventPlaced;
import com.decade.practice.threads.application.ports.out.EventRepository;
import com.decade.practice.threads.domain.ImageEvent;
import com.decade.practice.threads.domain.ImageSpec;
import com.decade.practice.threads.domain.MessageEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ImageFanoutEventPlacedListener extends AbstractFanoutEventPlacedListener<ImageEventPlaced> {


    public ImageFanoutEventPlacedListener(EventRepository events) {
        super(events);
    }

    @Override
    @ApplicationModuleListener
    public void on(ImageEventPlaced eventPlaced) {
        super.on(eventPlaced);
    }

    @Override
    protected MessageEvent newInstance(ImageEventPlaced eventPlaced, UUID ownerId) {
        return new ImageEvent(eventPlaced.getSenderId(), ownerId, eventPlaced.getSnapshot().chatId(),
                new ImageSpec(eventPlaced.getUri(), eventPlaced.getFilename(), eventPlaced.getWidth(), eventPlaced.getHeight(), eventPlaced.getFormat())
        );
    }
}
