package com.decade.practice.inbox.application.events;

import com.decade.practice.inbox.application.ports.out.ConversationRepository;
import com.decade.practice.inbox.application.ports.out.LogBroadCaster;
import com.decade.practice.inbox.domain.events.BatchInsertionEvent;
import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;

@Slf4j
public class KafkaBatchInsertionLogSaver extends BatchInsertionLogSaver {

    public KafkaBatchInsertionLogSaver(LogBroadCaster broadcaster, ConversationRepository conversations) {
        super(broadcaster, conversations);
        log.info("KafkaBatchInsertionLogSaver is enabled");
    }

    @Override
    @KafkaListener(topics = "batch-insertion-placed", groupId = "inbox")
    @Observed(name = "batch-insertion-placed",
        lowCardinalityKeyValues = {
            "batch-mode", "kafka"
        })
    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, multiplier = 2.0),
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    )
    public void on(BatchInsertionEvent event) {
        super.on(event);
    }

    @DltHandler
    public void handleDlt(BatchInsertionEvent event) {
        log.error("Failed to process BatchInsertionEvent after retries: {}", event);
    }
}
