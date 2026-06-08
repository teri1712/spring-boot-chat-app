package com.decade.practice.inbox.application.events;

import com.decade.practice.inbox.application.ports.out.ConversationRepository;
import com.decade.practice.inbox.application.ports.out.LogBroadCaster;
import com.decade.practice.inbox.domain.events.BatchInsertionEvent;
import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;

@Slf4j
public class ImmediateBatchInsertionLogSaver extends BatchInsertionLogSaver {

    public ImmediateBatchInsertionLogSaver(LogBroadCaster broadcaster, ConversationRepository conversations) {
        super(broadcaster, conversations);
        log.info("ImmediateBatchInsertionLogSaver is enabled");
    }

    @Override
    @Observed(name = "batch-insertion-placed",
        lowCardinalityKeyValues = {
            "batch-mode", "immediate"
        })
    @EventListener
    public void on(BatchInsertionEvent event) {
        super.on(event);
    }
}
