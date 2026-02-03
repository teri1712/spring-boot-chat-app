package com.decade.practice.application.events;

import com.decade.practice.application.services.outbox.OutboxStore;
import com.decade.practice.dto.events.MessageCreatedEvent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@AllArgsConstructor
public class MessageEventListener {
    private final OutboxStore outboxStore;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onMessageCreated(MessageCreatedEvent messageCreatedEvent) {
        outboxStore.save(messageCreatedEvent.getIdempotencyKey().toString(), "messages", messageCreatedEvent);
    }

}
