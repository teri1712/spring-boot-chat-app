package com.decade.practice.application.usecases;

import com.decade.practice.api.dto.EventDto;
import com.decade.practice.api.dto.EventRequest;
import com.decade.practice.api.dto.FileEventDto;
import com.decade.practice.persistence.jpa.entities.FileEvent;
import org.springframework.stereotype.Component;

@Component
public class FileEventFactory extends AbstractEventFactory<FileEvent> {

    @Override
    protected EventDto postInitEventResponse(FileEvent fileEvent, EventDto res) {
        res.setFileEvent(new FileEventDto(fileEvent.getFilename(), fileEvent.getSize(), fileEvent.getMediaUrl()));
        return res;
    }

    @Override
    public Class<FileEvent> getSupportedType() {
        return FileEvent.class;
    }

    @Override
    public FileEvent createEvent(EventRequest eventRequest) {
        FileEvent fileEvent = new FileEvent();
        fileEvent.setChatIdentifier(eventRequest.getChatIdentifier());
        fileEvent.setFilename(eventRequest.getFileEvent().getFilename());
        fileEvent.setMediaUrl(eventRequest.getFileEvent().getMediaUrl());
        fileEvent.setSize(eventRequest.getFileEvent().getSize());
        fileEvent.setEventType("FILE");
        return fileEvent;
    }

}
