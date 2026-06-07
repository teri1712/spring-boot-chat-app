package com.decade.practice.inbox.application.events;

import com.decade.practice.inbox.application.ports.out.ConversationRepository;
import com.decade.practice.inbox.application.ports.out.LogBroadCaster;
import com.decade.practice.inbox.application.ports.out.projection.ConversationView;
import com.decade.practice.inbox.domain.events.BatchInsertionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
public class BatchInsertionLogSaver {

    protected final LogBroadCaster broadcaster;
    protected final ConversationRepository conversations;

    public void on(BatchInsertionEvent event) {
        List<ConversationView> convos = conversations.findByChatIdBetweenParticipantIndex(event.insertion().chatId(), event.lower(), event.upper());
        broadcaster.broadcastInsert(event.insertion(), convos);
    }
}
