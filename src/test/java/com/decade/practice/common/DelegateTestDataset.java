package com.decade.practice.common;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Primary;

import java.util.List;

@TestComponent
@RequiredArgsConstructor
@Primary
public class DelegateTestDataset implements TestDataset {
    private final List<TestDataset> cleanups;

    @Override
    public void clean() {
        cleanups.forEach(TestDataset::clean);
    }

    @Override
    public void setup() {
        cleanups.forEach(TestDataset::setup);
    }
}
