package com.decade.practice.common;

import com.decade.practice.search.application.ports.out.HistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class MessageHistorySetTest implements TestDataset {
    private final HistoryRepository history;

    @Override
    public void clean() {
        history.deleteAll();
    }
}
