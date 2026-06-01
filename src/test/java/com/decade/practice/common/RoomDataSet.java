package com.decade.practice.common;

import com.decade.practice.inbox.application.ports.out.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class RoomDataSet implements TestDataSet {
    private final RoomRepository rooms;

    @Override
    public void clean() {
        rooms.deleteAll();
    }
}
