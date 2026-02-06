package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventResponse;
import com.decade.practice.dto.ImageEventResponse;
import com.decade.practice.persistence.jpa.entities.ImageEvent;
import org.springframework.stereotype.Component;

@Component

public class ImageEventConverter extends AbstractEventConverter<ImageEvent> {

    @Override
    protected EventResponse postInitEventResponse(ImageEvent event, EventResponse res) {
        ImageEventResponse imageEventResponse = null;
        if (event.getImage() != null) {
            imageEventResponse = new ImageEventResponse(
                    event.getImage().getUri(),
                    event.getImage().getFilename(),
                    event.getImage().getWidth(),
                    event.getImage().getHeight(),
                    event.getImage().getFormat()
            );
        }
        return new EventResponse(
                res.id(),
                res.idempotencyKey(),
                res.sender(),
                res.textEvent(),
                imageEventResponse,
                res.iconEvent(),
                res.preferenceEvent(),
                res.fileEvent(),
                res.seenEvent(),
                res.createdTime(),
                res.eventType(),
                res.eventVersion(),
                res.message(),
                res.owner(),
                res.partner(),
                res.chat()
        );
    }

    @Override
    public Class<ImageEvent> supports() {
        return ImageEvent.class;
    }
}
