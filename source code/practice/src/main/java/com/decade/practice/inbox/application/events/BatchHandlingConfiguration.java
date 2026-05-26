package com.decade.practice.inbox.application.events;

import com.decade.practice.inbox.application.ports.out.ConversationRepository;
import com.decade.practice.inbox.application.ports.out.LogBroadCaster;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class BatchHandlingConfiguration {


    @Bean
    @ConditionalOnMissingBean
    BatchParticipantLogSaver batchParticipantLogSaver(LogBroadCaster broadcaster, ConversationRepository conversations) {
        return new ImmediateBatchParticipantLogSaver(broadcaster, conversations);
    }

    @Bean
    @Profile("module-batch-handling")
    BatchParticipantLogSaver moduleBatchParticipantLogSaver(LogBroadCaster broadcaster, ConversationRepository conversations) {
        return new ModuleBatchParticipantLogSaver(broadcaster, conversations);
    }

    @Bean
    @Profile("kafka-batch-handling")
    BatchParticipantLogSaver kafkaBatchParticipantLogSaver(LogBroadCaster broadcaster, ConversationRepository conversations) {
        return new KafkaBatchParticipantLogSaver(broadcaster, conversations);
    }
}
