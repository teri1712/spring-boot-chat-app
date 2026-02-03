package com.decade.practice.persistence.jpa.entities;

import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event_type")
////////////////////////////////////////////////////////////////////

@Entity
@Getter
@Setter
public abstract class ChatEvent {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumns({
            @JoinColumn(name = "first_user", insertable = false, updatable = false), // referencedName derived
            @JoinColumn(name = "second_user", insertable = false, updatable = false) // referencedName derived
    })
    private Chat chat;

    @ManyToOne(cascade = CascadeType.PERSIST)
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

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(
                    name = "firstUser",
                    column = @Column(name = "first_user", updatable = false)
            ),
            @AttributeOverride(
                    name = "secondUser",
                    column = @Column(name = "second_user", updatable = false)
            )
    })
    private ChatIdentifier chatIdentifier;

    private int eventVersion = SyncContext.STARTING_VERSION;


    @Temporal(TemporalType.TIMESTAMP)
    private Instant createdTime = Instant.now();

    protected ChatEvent(Chat chat, User sender, String eventType) {
        this.chat = chat;
        this.sender = sender;
        this.eventType = eventType;
        this.owner = sender;
        this.chatIdentifier = chat.getIdentifier();
    }

    protected ChatEvent() {

    }

    public abstract ChatEvent clone();

    @Transient
    protected final Map<String, Object> extraProperties = new HashMap<>();

    public void setChat(Chat chat) {
        this.chat = chat;
        this.chatIdentifier = chat.getIdentifier();
    }

}
