package com.decade.practice.inbox.application.events;

import com.decade.practice.inbox.application.ports.out.ConversationRepository;
import com.decade.practice.inbox.application.ports.out.LogBroadCaster;
import com.decade.practice.inbox.domain.events.BatchInsertionEvent;
import com.decade.practice.inbox.domain.events.BatchUpdateEvent;
import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
public class KafkaBatchParticipantLogSaver extends BatchParticipantLogSaver {


    public KafkaBatchParticipantLogSaver(LogBroadCaster broadcaster, ConversationRepository conversations) {
        super(broadcaster, conversations);
        log.info("KafkaBatchParticipantLogSaver is enabled");
    }

    @Override
    @KafkaListener(topics = "batch-insertion-placed", groupId = "inbox")
    @Observed(name = "batch-insertion-placed",
        lowCardinalityKeyValues = {
            "batch-mode", "kafka"
        })
    public void on(BatchInsertionEvent event) {
        super.on(event);
    }

    @Override
    @KafkaListener(topics = "batch-update-placed", groupId = "inbox")
//    @RetryableTopic(
//        attempts = "3",
//        backoff = @Backoff(
//            delay = 1000,
//            multiplier = 2.0
//        )
//    )
    @Observed(name = "batch-update-placed",
        lowCardinalityKeyValues = {
            "batch-mode", "kafka"
        })
    public void on(BatchUpdateEvent event) {
        super.on(event);
    }


}
