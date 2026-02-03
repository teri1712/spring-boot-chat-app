package com.decade.practice.application.services.outbox;

public interface OutboxStore {
    void save(String key, String topic, Object content);
}
