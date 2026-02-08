package com.decade.practice.persistence.jpa.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event_type")
////////////////////////////////////////////////////////////////////

@Entity
@Getter
@Setter
public abstract class ChatEvent {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "chat_id", nullable = false)
    @NotNull
    private Chat chat;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private User sender;

    @Column(name = "event_type", insertable = false, updatable = false)
    private String eventType;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private User owner;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, updatable = false)
    @NotNull
    private UUID idempotentKey = UUID.randomUUID();


    @Column(name = "chat_id", nullable = false, updatable = false, insertable = false)
    @NotNull
    private String chatId;

    private int eventVersion = SyncContext.STARTING_VERSION;


    @Temporal(TemporalType.TIMESTAMP)
    private Instant createdTime = Instant.now();

    protected ChatEvent(Chat chat, User sender, String eventType) {
        this.chat = chat;
        this.sender = sender;
        this.eventType = eventType;
        this.owner = sender;
        this.chatId = chat.getIdentifier();
    }

    protected ChatEvent() {

    }

    public void setChat(Chat chat) {
        this.chat = chat;
        this.chatId = chat.getIdentifier();
    }

}
