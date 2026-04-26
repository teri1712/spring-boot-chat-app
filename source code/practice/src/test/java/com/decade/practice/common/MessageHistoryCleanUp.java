package com.decade.practice.common;

import com.decade.practice.search.application.ports.out.MessageHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class MessageHistoryCleanUp implements DataCleanUp {
    private final MessageHistoryRepository history;

    @Override
    public void clean() {
        history.deleteAll();
    }
}
