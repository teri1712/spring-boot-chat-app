package com.decade.practice.application.services.outbox;

import com.decade.practice.persistence.jpa.entities.Outbox;
import com.decade.practice.persistence.jpa.entities.OutboxStatus;
import com.decade.practice.persistence.jpa.repositories.OutboxRepository;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class OutboxWorker {

    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final OutboxRepository outboxRepository;


    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void fetchAndHandleOutboxes() {
        outboxRepository.findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING).forEach(this::handleOutbox);
    }

    private void handleOutbox(Outbox outbox) {
        kafkaTemplate.send(outbox.getTopic(), outbox.getKey(), outbox.getPayload());
    }

}
