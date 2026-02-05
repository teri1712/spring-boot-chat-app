package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventDto;
import com.decade.practice.dto.FileEventDto;
import com.decade.practice.persistence.jpa.entities.FileEvent;
import org.springframework.stereotype.Component;

@Component
public class FileEventConverter extends AbstractEventConverter<FileEvent> {

    @Override
    protected EventDto postInitEventResponse(FileEvent fileEvent, EventDto res) {
        res.setFileEvent(new FileEventDto(fileEvent.getFilename(), fileEvent.getSize(), fileEvent.getMediaUrl()));
        return res;
    }

    @Override
    public Class<FileEvent> supports() {
        return FileEvent.class;
    }
}
