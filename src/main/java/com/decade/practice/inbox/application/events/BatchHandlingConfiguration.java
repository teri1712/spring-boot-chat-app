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
    BatchParticipantLogSaver batchParticipantLogSaver(LogBroadCaster broadcaster, ConversationRepository conversations) {
        return new ImmediateBatchParticipantLogSaver(broadcaster, conversations);
    }

    @Bean
    @ConditionalOnProperty(
        name = "batch.mode",
        havingValue = "module",
        matchIfMissing = true
    )
    BatchParticipantLogSaver moduleBatchParticipantLogSaver(LogBroadCaster broadcaster, ConversationRepository conversations) {
        return new ModuleBatchParticipantLogSaver(broadcaster, conversations);
    }

    @Bean
    @ConditionalOnProperty(
        name = "batch.mode",
        havingValue = "kafka",
        matchIfMissing = true
    )
    BatchParticipantLogSaver kafkaBatchParticipantLogSaver(LogBroadCaster broadcaster, ConversationRepository conversations) {
        return new KafkaBatchParticipantLogSaver(broadcaster, conversations);
    }
}
