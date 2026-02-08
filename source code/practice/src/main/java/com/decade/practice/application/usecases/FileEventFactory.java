package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventCreateCommand;
import com.decade.practice.dto.FileEventCreateCommand;
import com.decade.practice.persistence.jpa.entities.FileEvent;
import com.decade.practice.persistence.jpa.repositories.ChatRepository;
import com.decade.practice.persistence.jpa.repositories.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class FileEventFactory extends EventFactory<FileEvent> {


    protected FileEventFactory(UserRepository userRepository, ChatRepository chatRepository) {
        super(userRepository, chatRepository);
    }

    @Override
    public FileEvent newInstance(EventCreateCommand command) {
        FileEventCreateCommand fileEventCreateCommand = (FileEventCreateCommand) command;
        FileEvent fileEvent = new FileEvent();
        fileEvent.setFilename(fileEventCreateCommand.getFilename());
        fileEvent.setMediaUrl(fileEventCreateCommand.getMediaUrl());
        fileEvent.setSize(fileEventCreateCommand.getSize());
        fileEvent.setEventType("FILE");
        return fileEvent;
    }

    @Override
    public boolean supports(EventCreateCommand command) {
        return command instanceof FileEventCreateCommand;
    }
}
