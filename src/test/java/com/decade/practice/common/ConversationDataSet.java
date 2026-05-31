package com.decade.practice.common;

import com.decade.practice.inbox.application.ports.out.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class ConversationDataSet implements TestDataSet {
    private final ConversationRepository conversations;

    @Override
    public void clean() {
        conversations.deleteAll();
    }
}
