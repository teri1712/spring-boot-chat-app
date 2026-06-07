package com.decade.practice.inbox.application.events;

import com.decade.practice.inbox.application.ports.out.ConversationRepository;
import com.decade.practice.inbox.application.ports.out.LogBroadCaster;
import com.decade.practice.inbox.domain.events.BatchUpdateEvent;
import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;

@Slf4j
public class ImmediateBatchUpdateLogSaver extends BatchUpdateLogSaver {

    public ImmediateBatchUpdateLogSaver(LogBroadCaster broadcaster, ConversationRepository conversations) {
        super(broadcaster, conversations);
        log.info("ImmediateBatchUpdateLogSaver is enabled");
    }

    @Override
    @Observed(name = "batch-update-placed",
        lowCardinalityKeyValues = {
            "batch-mode", "immediate"
        })
    @EventListener
    public void on(BatchUpdateEvent event) {
        super.on(event);
    }
}
