package com.decade.practice.application.services;

import com.decade.practice.application.services.outbox.OutboxStore;
import com.decade.practice.persistence.jpa.entities.Outbox;
import com.decade.practice.persistence.jpa.repositories.OutboxRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OutboxStoreImpl implements OutboxStore {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void save(String key, String topic, Object content) {
        Outbox outbox = new Outbox();
        outbox.setKey(key);
        outbox.setTopic(topic);
        outbox.setPayload(objectMapper.convertValue(content, new TypeReference<>() {
        }));
        outboxRepository.save(outbox);
    }
}
