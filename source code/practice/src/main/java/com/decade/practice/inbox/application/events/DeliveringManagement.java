package com.decade.practice.inbox.application.events;

import com.decade.practice.inbox.domain.events.MessageCreated;
import com.decade.practice.inbox.domain.events.MessageUpdated;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FanoutHandler {

    private final

    @ApplicationModuleListener(id = "fanout-insert")
    void on(MessageCreated event) {
    }

    @ApplicationModuleListener(id = "fanout-update")
    void on(MessageUpdated event) {
    }
}
