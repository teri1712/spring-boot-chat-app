package com.decade.practice.inbox.application.events;

import com.decade.practice.inbox.application.ports.out.MessageRepository;
import com.decade.practice.inbox.domain.Icon;
import com.decade.practice.inbox.domain.events.IconRoomEventCreated;
import lombok.AllArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class IconListener {

    private final MessageRepository messages;

    @ApplicationModuleListener(id = "icon_listener")
    public void on(IconRoomEventCreated eventPlaced) {
        messages.save(new Icon(eventPlaced.getChatEventId(), eventPlaced.getSenderId(), eventPlaced.getChatId(), eventPlaced.getCreatedAt(), eventPlaced.getIconId()));
    }
}
