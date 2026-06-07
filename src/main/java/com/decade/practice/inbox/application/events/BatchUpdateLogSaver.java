package com.decade.practice.inbox.application.events;

import com.decade.practice.inbox.application.ports.out.ConversationRepository;
import com.decade.practice.inbox.application.ports.out.LogBroadCaster;
import com.decade.practice.inbox.application.ports.out.projection.ConversationView;
import com.decade.practice.inbox.domain.events.BatchUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
public class BatchUpdateLogSaver {

    protected final LogBroadCaster broadcaster;
    protected final ConversationRepository conversations;

    public void on(BatchUpdateEvent event) {
        List<ConversationView> convos = conversations.findByChatIdBetweenParticipantIndex(event.update().chatId(), event.lower(), event.upper());
        broadcaster.broadcastUpdate(event.update(), convos);
    }
}
