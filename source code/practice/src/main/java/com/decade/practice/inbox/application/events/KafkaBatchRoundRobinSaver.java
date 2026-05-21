package com.decade.practice.inbox.application.events;

import com.decade.practice.inbox.application.ports.out.ConversationRepository;
import com.decade.practice.inbox.application.ports.out.LogBroadCaster;
import com.decade.practice.inbox.domain.events.BatchInsertionEvent;
import com.decade.practice.inbox.domain.events.BatchUpdateEvent;
import io.micrometer.observation.annotation.Observed;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Profile("kafka-batch-handling")
public class KafkaBatchRoundRobinSaver extends BatchRoundRobinSaver {


    public KafkaBatchRoundRobinSaver(LogBroadCaster broadcaster, ConversationRepository conversations) {
        super(broadcaster, conversations);
    }

    @Override
    @KafkaListener(topics = "batch-insertion-placed", groupId = "inbox", concurrency = "4")
    @Observed(name = "batch-insertion-placed",
        lowCardinalityKeyValues = {
            "batch-mode", "kafka"
        })
    public void on(BatchInsertionEvent event) {
        super.on(event);
    }

    @Override
    @KafkaListener(topics = "batch-update-placed", groupId = "inbox", concurrency = "4")
    @Observed(name = "batch-update-placed",
        lowCardinalityKeyValues = {
            "batch-mode", "kafka"
        })
    public void on(BatchUpdateEvent event) {
        super.on(event);
    }
}
