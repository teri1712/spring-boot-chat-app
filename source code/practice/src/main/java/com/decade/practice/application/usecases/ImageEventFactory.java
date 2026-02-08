package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventCreateCommand;
import com.decade.practice.dto.ImageEventCreateCommand;
import com.decade.practice.persistence.jpa.embeddables.ImageSpecEmbeddable;
import com.decade.practice.persistence.jpa.entities.ImageEvent;
import com.decade.practice.persistence.jpa.repositories.ChatRepository;
import com.decade.practice.persistence.jpa.repositories.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class ImageEventFactory extends EventFactory<ImageEvent> {


    protected ImageEventFactory(UserRepository userRepository, ChatRepository chatRepository) {
        super(userRepository, chatRepository);
    }

    @Override
    public ImageEvent newInstance(EventCreateCommand command) {
        ImageEventCreateCommand imageEventCreateCommand = (ImageEventCreateCommand) command;
        ImageEvent imageEvent = new ImageEvent();

        ImageSpecEmbeddable imageSpec = new ImageSpecEmbeddable();
        imageSpec.setUri(imageEventCreateCommand.getDownloadUrl());
        imageSpec.setFilename(imageEventCreateCommand.getFilename());
        imageSpec.setWidth(imageEventCreateCommand.getWidth());
        imageSpec.setHeight(imageEventCreateCommand.getHeight());
        imageSpec.setFormat(imageEventCreateCommand.getFormat());
        imageEvent.setImage(imageSpec);
        imageEvent.setEventType("IMAGE");
        return imageEvent;
    }

    @Override
    public boolean supports(EventCreateCommand command) {
        return command instanceof ImageEventCreateCommand;
    }
}
