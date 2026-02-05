package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventRequest;
import com.decade.practice.persistence.jpa.embeddables.ImageSpec;
import com.decade.practice.persistence.jpa.entities.ImageEvent;
import org.springframework.stereotype.Component;

@Component
public class ImageEventFactory implements EventFactory<ImageEvent> {

    @Override
    public ImageEvent newInstance(EventRequest eventRequest) {
        ImageEvent imageEvent = new ImageEvent();
        ImageSpec imageSpec = new ImageSpec();
        imageSpec.setUri(eventRequest.getImageEvent().getDownloadUrl());
        imageSpec.setFilename(eventRequest.getImageEvent().getFilename());
        imageSpec.setWidth(eventRequest.getImageEvent().getWidth());
        imageSpec.setHeight(eventRequest.getImageEvent().getHeight());
        imageSpec.setFormat(eventRequest.getImageEvent().getFormat());
        imageEvent.setImage(imageSpec);
        imageEvent.setEventType("IMAGE");
        return imageEvent;
    }

    @Override
    public boolean supports(EventRequest eventRequest) {
        return eventRequest.getImageEvent() != null;
    }
}
