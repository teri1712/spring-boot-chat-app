package com.decade.practice.application.usecases;

import com.decade.practice.dto.EventCreateCommand;
import com.decade.practice.dto.IconEventCreateCommand;
import com.decade.practice.persistence.jpa.entities.IconEvent;
import com.decade.practice.persistence.jpa.repositories.ChatRepository;
import com.decade.practice.persistence.jpa.repositories.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class IconEventFactory extends EventFactory<IconEvent> {

    protected IconEventFactory(UserRepository userRepository, ChatRepository chatRepository) {
        super(userRepository, chatRepository);
    }

    @Override
    public IconEvent newInstance(EventCreateCommand command) {
        IconEventCreateCommand iconEventCreateCommand = (IconEventCreateCommand) command;
        IconEvent iconEvent = new IconEvent();

        iconEvent.setIconId(iconEventCreateCommand.getIconId());
        iconEvent.setEventType("ICON");
        return iconEvent;
    }

    @Override
    public boolean supports(EventCreateCommand command) {
        return command instanceof IconEventCreateCommand;
    }
}
