package com.decade.practice.common;

public interface TestDataset {
    void clean();

    default void setup() {
    }
}
