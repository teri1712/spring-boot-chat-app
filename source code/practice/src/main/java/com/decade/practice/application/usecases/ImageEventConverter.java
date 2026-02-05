package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventDto;
import com.decade.practice.dto.ImageEventDto;
import com.decade.practice.persistence.jpa.entities.ImageEvent;
import org.springframework.stereotype.Component;

@Component

public class ImageEventConverter extends AbstractEventConverter<ImageEvent> {

    @Override
    protected EventDto postInitEventResponse(ImageEvent event, EventDto res) {
        ImageEventDto imageEventDto = new ImageEventDto();
        if (event.getImage() != null) {
            imageEventDto.setFilename(event.getImage().getFilename());
            imageEventDto.setDownloadUrl(event.getImage().getUri());
            imageEventDto.setFormat(event.getImage().getFormat());
            imageEventDto.setWidth(event.getImage().getWidth());
            imageEventDto.setHeight(event.getImage().getHeight());
        }
        res.setImageEvent(imageEventDto);
        return res;
    }

    @Override
    public Class<ImageEvent> supports() {
        return ImageEvent.class;
    }
}
