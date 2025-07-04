package com.decade.practice.model.domain.entity;

import com.decade.practice.model.domain.SyncContext;
import com.decade.practice.model.domain.embeddable.ChatIdentifier;
import com.decade.practice.model.local.LocalChat;
import com.decade.practice.utils.ChatUtils;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "eventType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SeenEvent.class, name = "SEEN"),
        @JsonSubTypes.Type(value = TextEvent.class, name = "TEXT"),
        @JsonSubTypes.Type(value = IconEvent.class, name = "ICON"),
        @JsonSubTypes.Type(value = ImageEvent.class, name = "IMAGE"),
        @JsonSubTypes.Type(value = WelcomeEvent.class, name = "HELLO WORLD")
})
@Entity
@Table(indexes = @Index(columnList = "event_version"))
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event_type")
public abstract class ChatEvent {

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumns({
            @JoinColumn(name = "first_user", insertable = false, updatable = false), // referencedName derived
            @JoinColumn(name = "second_user", insertable = false, updatable = false) // referencedName derived
    })
    private Chat chat;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.PERSIST)
    private User sender;

    @Column(name = "event_type", insertable = false, updatable = false)
    private final String eventType;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ManyToOne(cascade = CascadeType.PERSIST)
    private User owner;

    @JsonDeserialize(as = HashSet.class)
    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "event", fetch = FetchType.EAGER)
    private final Set<Edge> edges = new HashSet<>();

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonProperty(value = "id")
    @Column(nullable = false, unique = true)
    @NotNull
    private UUID localId = UUID.randomUUID();

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
    private long createdTime = System.currentTimeMillis();

    // No-arg constructor required by JPA
    protected ChatEvent() {
        this.eventType = null;
    }

    protected ChatEvent(Chat chat, User sender, String eventType) {
        this.chat = chat;
        this.sender = sender;
        this.eventType = eventType;
        this.owner = sender;
        this.chatIdentifier = chat.getIdentifier();
    }

    public abstract ChatEvent copy();

    @JsonGetter("partner")
    public User getPartner() {
        return ChatUtils.inspectPartner(chat, owner);
    }

    @JsonGetter("chat")
    public LocalChat getLocalChat() {
        return new LocalChat(chat, owner);
    }

    @JsonGetter("sender")
    public UUID getSenderId() {
        return sender.getId();
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
        this.chatIdentifier = chat.getIdentifier();
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getEventType() {
        return eventType;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getLocalId() {
        return localId;
    }

    public void setLocalId(UUID localId) {
        this.localId = localId;
    }

    public ChatIdentifier getChatIdentifier() {
        return chatIdentifier;
    }

    public int getEventVersion() {
        return eventVersion;
    }

    public void setEventVersion(int eventVersion) {
        this.eventVersion = eventVersion;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }
}
