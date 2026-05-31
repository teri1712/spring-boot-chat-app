package com.decade.practice.inbox.domain;

import com.decade.practice.inbox.domain.events.InboxLogCreated;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class InboxLog extends AbstractAggregateRoot<InboxLog> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inbox_log_sequence")
    @SequenceGenerator(name = "inbox_log_sequence", sequenceName = "inbox_log_seq", initialValue = 1000)
    private Long sequenceId;

    private UUID senderId;
    private UUID ownerId;
    private Long conversationId;
    private Long messageId;

    public InboxLog(LogAction action, UUID senderId, UUID ownerId, Long conversationId, Long messageId) {
        this.action = action;
        this.senderId = senderId;
        this.conversationId = conversationId;
        this.messageId = messageId;
        this.ownerId = ownerId;
    }

    @PrePersist
    void onPersisted() {
        registerEvent(new InboxLogCreated(sequenceId, conversationId, messageId, senderId, ownerId, action));
    }

    @Enumerated(EnumType.STRING)
    private LogAction action;


}
