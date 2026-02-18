package com.decade.practice.threads.application.events;

import com.decade.practice.engagement.api.events.FileEventPlaced;
import com.decade.practice.threads.application.ports.out.EventRepository;
import com.decade.practice.threads.domain.FileEvent;
import com.decade.practice.threads.domain.MessageEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FileFanoutEventPlacedListener extends AbstractFanoutEventPlacedListener<FileEventPlaced> {


    public FileFanoutEventPlacedListener(EventRepository events) {
        super(events);
    }

    @Override
    @ApplicationModuleListener
    public void on(FileEventPlaced eventPlaced) {
        super.on(eventPlaced);
    }

    @Override
    protected MessageEvent newInstance(FileEventPlaced eventPlaced, UUID ownerId) {
        return new FileEvent(eventPlaced.getSenderId(), ownerId, eventPlaced.getSnapshot().chatId(), eventPlaced.getFilename(), eventPlaced.getUri(), eventPlaced.getSize());
    }
}
