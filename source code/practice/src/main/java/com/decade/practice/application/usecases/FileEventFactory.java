package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventRequest;
import com.decade.practice.persistence.jpa.entities.FileEvent;
import org.springframework.stereotype.Component;

@Component

public class FileEventFactory implements EventFactory<FileEvent> {

    @Override
    public FileEvent newInstance(EventRequest eventRequest) {
        FileEvent fileEvent = new FileEvent();
        fileEvent.setFilename(eventRequest.getFileEvent().getFilename());
        fileEvent.setMediaUrl(eventRequest.getFileEvent().getMediaUrl());
        fileEvent.setSize(eventRequest.getFileEvent().getSize());
        fileEvent.setEventType("FILE");
        return fileEvent;
    }

    @Override
    public boolean supports(EventRequest eventRequest) {
        return eventRequest.getFileEvent() != null;
    }
}
