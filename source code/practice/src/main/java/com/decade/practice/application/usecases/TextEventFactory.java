package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventCreateCommand;
import com.decade.practice.dto.TextEventCreateCommand;
import com.decade.practice.persistence.jpa.entities.TextEvent;
import com.decade.practice.persistence.jpa.repositories.ChatRepository;
import com.decade.practice.persistence.jpa.repositories.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class TextEventFactory extends EventFactory<TextEvent> {


    protected TextEventFactory(UserRepository userRepository, ChatRepository chatRepository) {
        super(userRepository, chatRepository);
    }

    @Override
    public TextEvent newInstance(EventCreateCommand command) {
        TextEvent textEvent = new TextEvent();
        textEvent.setContent(((TextEventCreateCommand) command).getContent());
        textEvent.setEventType("TEXT");
        return textEvent;
    }

    @Override
    public boolean supports(EventCreateCommand command) {
        return command instanceof TextEventCreateCommand;
    }
}
