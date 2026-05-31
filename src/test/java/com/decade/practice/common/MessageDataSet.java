package com.decade.practice.common;

import com.decade.practice.inbox.application.ports.out.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class MessageDataSet implements TestDataSet {
    private final MessageRepository messages;

    @Override
    public void clean() {
        messages.deleteAll();
    }
}
