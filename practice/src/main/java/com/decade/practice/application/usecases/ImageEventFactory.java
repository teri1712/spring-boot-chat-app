package com.decade.practice.application.usecases;

import com.decade.practice.api.dto.EventDto;
import com.decade.practice.api.dto.EventRequest;
import com.decade.practice.api.dto.ImageEventDto;
import com.decade.practice.persistence.jpa.embeddables.ImageSpec;
import com.decade.practice.persistence.jpa.entities.ImageEvent;
import org.springframework.stereotype.Component;

@Component
public class ImageEventFactory extends AbstractEventFactory<ImageEvent> {

    @Override
    public Class<ImageEvent> getSupportedType() {
        return ImageEvent.class;
    }

    @Override
    public ImageEvent createEvent(EventRequest eventRequest) {
        ImageEvent imageEvent = new ImageEvent();
        imageEvent.setChatIdentifier(eventRequest.getChatIdentifier());
        ImageSpec imageSpec = new ImageSpec();
        imageSpec.setUri(eventRequest.getImageEvent().getUri());
        imageSpec.setFilename(eventRequest.getImageEvent().getFilename());
        imageSpec.setWidth(eventRequest.getImageEvent().getWidth());
        imageSpec.setHeight(eventRequest.getImageEvent().getHeight());
        imageSpec.setFormat(eventRequest.getImageEvent().getFormat());
        imageEvent.setImage(imageSpec);
        imageEvent.setEventType("IMAGE");
        return imageEvent;
    }

    @Override
    protected EventDto postInitEventResponse(ImageEvent event, EventDto res) {
        ImageEventDto imageEventDto = new ImageEventDto();
        if (event.getImage() != null) {
            imageEventDto.setFilename(event.getImage().getFilename());
            imageEventDto.setUri(event.getImage().getUri());
            imageEventDto.setFormat(event.getImage().getFormat());
            imageEventDto.setWidth(event.getImage().getWidth());
            imageEventDto.setHeight(event.getImage().getHeight());
        }
        res.setImageEvent(imageEventDto);
        return res;
    }

}
