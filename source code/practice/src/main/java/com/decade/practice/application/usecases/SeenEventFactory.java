package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventCreateCommand;
import com.decade.practice.dto.SeenEventCreateCommand;
import com.decade.practice.persistence.jpa.entities.SeenEvent;
import com.decade.practice.persistence.jpa.repositories.ChatRepository;
import com.decade.practice.persistence.jpa.repositories.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class SeenEventFactory extends EventFactory<SeenEvent> {

    protected SeenEventFactory(UserRepository userRepository, ChatRepository chatRepository) {
        super(userRepository, chatRepository);
    }

    @Override
    public SeenEvent newInstance(EventCreateCommand command) {
        SeenEventCreateCommand seenEventCreateCommand = (SeenEventCreateCommand) command;
        SeenEvent seenEvent = new SeenEvent();
        seenEvent.setAt(seenEventCreateCommand.getAt());
        seenEvent.setEventType("SEEN");
        return seenEvent;
    }

    @Override
    public boolean supports(EventCreateCommand command) {
        return command instanceof SeenEventCreateCommand;
    }
}
