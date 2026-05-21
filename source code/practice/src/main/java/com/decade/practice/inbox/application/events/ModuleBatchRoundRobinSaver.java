package com.decade.practice.inbox.application.events;

import com.decade.practice.inbox.application.ports.out.ConversationRepository;
import com.decade.practice.inbox.application.ports.out.LogBroadCaster;
import com.decade.practice.inbox.domain.events.BatchInsertionEvent;
import com.decade.practice.inbox.domain.events.BatchUpdateEvent;
import io.micrometer.observation.annotation.Observed;
import org.springframework.context.annotation.Profile;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
@Profile("!kafka-batch-handling")
public class ModuleBatchRoundRobinSaver extends BatchRoundRobinSaver {


    public ModuleBatchRoundRobinSaver(LogBroadCaster broadcaster, ConversationRepository conversations) {
        super(broadcaster, conversations);
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

    @Override
    @ApplicationModuleListener(id = "batch-update-placed")
    @Observed(name = "batch-update-placed",
        lowCardinalityKeyValues = {
            "batch-mode", "module"
        })
    public void on(BatchUpdateEvent event) {
        super.on(event);
    }
}
