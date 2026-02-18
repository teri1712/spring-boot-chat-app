package com.decade.practice.threads.domain;

import com.decade.practice.threads.domain.events.EventCreated;
import com.decade.practice.threads.domain.events.EventReady;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.Instant;
import java.util.UUID;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event_type")
////////////////////////////////////////////////////////////////////

@Entity
@Getter
public abstract class ChatEvent extends AbstractAggregateRoot<ChatEvent> {

    @Id
    private UUID id;

    @Column(updatable = false, nullable = false)
    private UUID senderId;


    @Column(name = "event_type", insertable = false, updatable = false)
    private String eventType;

    @Column(updatable = false, nullable = false)
    private UUID ownerId;

    @Version
    private Integer version;


    ChatEvent(UUID senderId, String eventType, UUID ownerId, String chatId, String roomNameSnapshot, String roomAvatarSnapshot) {
        this.id = UUID.randomUUID();
        this.senderId = senderId;
        this.eventType = eventType;
        this.ownerId = ownerId;
        this.chatId = chatId;
        this.createdAt = Instant.now();
        this.roomNameSnapshot = roomNameSnapshot;
        this.roomAvatarSnapshot = roomAvatarSnapshot;
        registerEvent(new EventCreated(id, senderId, eventType, ownerId, chatId, createdAt));
    }

    protected ChatEvent() {
    }

    @Column(nullable = false, updatable = false)
    @NotNull
    private String chatId;

    private String roomNameSnapshot;
    private String roomAvatarSnapshot;

    private Integer eventVersion;

    public void setEventVersion(Integer eventVersion) {
        if (this.eventVersion != null) {
            throw new IllegalStateException("Event version already set");
        }
        this.eventVersion = eventVersion;

        registerEvent(new EventReady(id, senderId, eventType, ownerId, chatId, createdAt, eventVersion));

    }

    @Temporal(TemporalType.TIMESTAMP)
    private Instant createdAt;


    protected boolean isMine() {
        return senderId.equals(ownerId);
    }

}
