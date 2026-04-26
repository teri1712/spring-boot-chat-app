package com.decade.practice.common;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Primary;

import java.util.List;

@TestComponent
@RequiredArgsConstructor
@Primary
public class DelegateDataCleanUp implements DataCleanUp {
    private final List<DataCleanUp> cleanups;

    @Override
    public void clean() {
        cleanups.forEach(DataCleanUp::clean);
    }
}
