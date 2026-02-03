package com.decade.practice.application.services.outbox;

import com.decade.practice.persistence.jpa.entities.Outbox;
import com.decade.practice.persistence.jpa.entities.OutboxStatus;
import com.decade.practice.persistence.jpa.repositories.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@AllArgsConstructor
public class OutboxWorker {

    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final OutboxRepository outboxRepository;


    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void fetchAndHandleOutboxes() {
        outboxRepository.findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING).forEach(this::handleOutbox);
    }

    private void handleOutbox(Outbox outbox) {
        try {
            kafkaTemplate.send(outbox.getTopic(), outbox.getKey(), objectMapper.writeValueAsString(outbox.getPayload()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        outbox.setStatus(OutboxStatus.SENT);
        log.trace("Sent message to topic: {} with key: {}", outbox.getTopic(), outbox.getKey());
    }

}
