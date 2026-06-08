package com.decade.practice.inbox.application.events;

import com.decade.practice.inbox.application.ports.out.ConversationRepository;
import com.decade.practice.inbox.application.ports.out.LogBroadCaster;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchHandlingConfiguration {


    @Bean
    @ConditionalOnProperty(
        name = "batch.mode",
        havingValue = "immediate",
        matchIfMissing = true
    )
    BatchInsertionLogSaver batchInsertionLogSaver(LogBroadCaster broadcaster, ConversationRepository conversations) {
        return new ImmediateBatchInsertionLogSaver(broadcaster, conversations);
    }

    @Bean
    @ConditionalOnProperty(
        name = "batch.mode",
        havingValue = "immediate",
        matchIfMissing = true
    )
    BatchUpdateLogSaver batchUpdateLogSaver(LogBroadCaster broadcaster, ConversationRepository conversations) {
        return new ImmediateBatchUpdateLogSaver(broadcaster, conversations);
    }

    @Bean
    @ConditionalOnProperty(
        name = "batch.mode",
        havingValue = "module",
        matchIfMissing = true
    )
    BatchInsertionLogSaver moduleBatchInsertionLogSaver(LogBroadCaster broadcaster, ConversationRepository conversations) {
        return new ModuleBatchInsertionLogSaver(broadcaster, conversations);
    }

    @Bean
    @ConditionalOnProperty(
        name = "batch.mode",
        havingValue = "module",
        matchIfMissing = true
    )
    BatchUpdateLogSaver moduleBatchUpdateLogSaver(LogBroadCaster broadcaster, ConversationRepository conversations) {
        return new ModuleBatchUpdateLogSaver(broadcaster, conversations);
    }

    @Bean
    @ConditionalOnProperty(
        name = "batch.mode",
        havingValue = "kafka",
        matchIfMissing = true
    )
    BatchInsertionLogSaver kafkaBatchInsertionLogSaver(LogBroadCaster broadcaster, ConversationRepository conversations) {
        return new KafkaBatchInsertionLogSaver(broadcaster, conversations);
    }

    @Bean
    @ConditionalOnProperty(
        name = "batch.mode",
        havingValue = "kafka",
        matchIfMissing = true
    )
    BatchUpdateLogSaver kafkaBatchUpdateLogSaver(LogBroadCaster broadcaster, ConversationRepository conversations) {
        return new KafkaBatchUpdateLogSaver(broadcaster, conversations);
    }
}
