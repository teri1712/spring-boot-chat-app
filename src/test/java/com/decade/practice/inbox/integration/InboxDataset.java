package com.decade.practice.inbox.integration;

import com.decade.practice.common.TestDataset;
import com.decade.practice.inbox.application.ports.out.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class InboxDataset implements TestDataset {
    final LogRepository logs;
    final RoomEventRepository roomEvents;
    final ConversationRepository conversations;
    final RoomRepository rooms;
    final MessageRepository messages;

    @Override
    public void clean() {
        roomEvents.deleteAll();
        logs.deleteAll();
        messages.deleteAll();
        conversations.deleteAll();
        rooms.deleteAll();
    }
}
