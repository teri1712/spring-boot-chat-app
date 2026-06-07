package com.decade.practice.inbox.application.events;

import com.decade.practice.inbox.application.ports.out.ConversationRepository;
import com.decade.practice.inbox.application.ports.out.LogBroadCaster;
import com.decade.practice.inbox.domain.events.BatchInsertionEvent;
import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;

@Slf4j
public class ModuleBatchInsertionLogSaver extends BatchInsertionLogSaver {

    public ModuleBatchInsertionLogSaver(LogBroadCaster broadcaster, ConversationRepository conversations) {
        super(broadcaster, conversations);
        log.info("ModuleBatchInsertionLogSaver is enabled");
    }

    @Override
    @ApplicationModuleListener(id = "batch-insertion-placed")
    @Observed(name = "batch-insertion-placed",
        lowCardinalityKeyValues = {
            "batch-mode", "module"
        })
    public void on(BatchInsertionEvent event) {
        super.on(event);
    }
}
