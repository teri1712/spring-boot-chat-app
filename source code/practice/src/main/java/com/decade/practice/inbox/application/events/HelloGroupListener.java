package com.decade.practice.inbox.application.events;

import com.decade.practice.inbox.application.ports.out.MessageRepository;
import com.decade.practice.inbox.domain.HelloGroup;
import com.decade.practice.inbox.domain.events.RoomCreated;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class HelloGroupListener {

    private final MessageRepository messages;

    @ApplicationModuleListener(id = "group_listener")
    public void on(RoomCreated event) {
        if (event.representatives().size() > 2) {
            HelloGroup message = new HelloGroup(
                UUID.randomUUID(),
                event.creator(),
                event.chatId(),
                event.at());
            messages.save(message);
        }
    }
}
