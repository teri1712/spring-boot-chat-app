package com.decade.practice.inbox.application.events;

import com.decade.practice.inbox.application.ports.out.ConversationRepository;
import com.decade.practice.inbox.application.ports.out.LogBroadCaster;
import com.decade.practice.inbox.domain.events.BatchInsertionEvent;
import com.decade.practice.inbox.domain.events.BatchUpdateEvent;
import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;

@Slf4j
public class ImmediateBatchParticipantLogSaver extends BatchParticipantLogSaver {

    public ImmediateBatchParticipantLogSaver(LogBroadCaster broadcaster, ConversationRepository conversations) {
        super(broadcaster, conversations);
        log.info("ImmediateBatchParticipantLogSaver is enabled");
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
