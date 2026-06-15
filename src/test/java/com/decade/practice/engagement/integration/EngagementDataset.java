package com.decade.practice.engagement.integration;

import com.decade.practice.common.TestDataset;
import com.decade.practice.engagement.application.ports.out.ChatRepository;
import com.decade.practice.engagement.application.ports.out.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class EngagementDataset implements TestDataset {
    final ChatRepository chats;
    final ParticipantRepository participants;

    @Override
    public void clean() {
        chats.deleteAll();
        participants.deleteAll();
    }
}
