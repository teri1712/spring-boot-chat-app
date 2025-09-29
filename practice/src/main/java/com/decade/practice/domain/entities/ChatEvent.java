package com.decade.practice.domain.entities;

import com.decade.practice.domain.embeddables.ChatIdentifier;
import com.decade.practice.domain.locals.Chat;
import com.decade.practice.utils.ChatUtils;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.*;

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
        @JsonSubTypes.Type(value = FileEvent.class, name = "FILE"),
        @JsonSubTypes.Type(value = WelcomeEvent.class, name = "HELLO WORLD"),
        @JsonSubTypes.Type(value = PreferenceEvent.class, name = "PREFERENCE"),
})
////////////////////////////////////////////////////////////////////
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
////////////////////////////////////////////////////////////////////

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event_type")
////////////////////////////////////////////////////////////////////

@Entity
@Table(indexes = {@Index(columnList = "event_version"),
        @Index(columnList = "event_version, owner_id"),
        @Index(columnList = "event_version, owner_id, first_user, second_user")})

public abstract class ChatEvent {

        @JsonIgnore
        @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
        @JoinColumns({
                @JoinColumn(name = "first_user", insertable = false, updatable = false), // referencedName derived
                @JoinColumn(name = "second_user", insertable = false, updatable = false) // referencedName derived
        })
        private com.decade.practice.domain.entities.Chat chat;

        @JsonIgnore
        @ManyToOne(cascade = CascadeType.PERSIST)
        private User sender;

        @Column(name = "event_type", insertable = false, updatable = false)
        private String eventType;

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        @ManyToOne(cascade = CascadeType.PERSIST)
        private User owner;

        @JsonIgnore
        @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "event", fetch = FetchType.EAGER)
        private Collection<Edge> edges = new ArrayList<>();

        @JsonIgnore
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;

        @NotNull
        @Valid
        @Embedded
        private Receipt receipt = new Receipt();

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

        private long createdTime = System.currentTimeMillis();

        protected ChatEvent(com.decade.practice.domain.entities.Chat chat, User sender, String eventType) {
                this.chat = chat;
                this.sender = sender;
                this.eventType = eventType;
                this.owner = sender;
                this.chatIdentifier = chat.getIdentifier();
        }

        protected ChatEvent() {

        }

        public abstract ChatEvent copy();

        @Transient
        protected final Map<String, Object> extraProperties = new HashMap<>();

        @PostLoad
        @PostPersist
        protected void bindExtraProperties() {
                extraProperties.put("partner", ChatUtils.inspectPartner(getChat(), getOwner()));
                extraProperties.put("chat", new Chat(getChat(), getOwner()));
                extraProperties.put("sender", sender.getId());
        }

        @JsonAnyGetter
        public Map<String, Object> getExtraProperties() {
                return extraProperties;
        }

        @JsonAnySetter
        public void addExtraProperty(String key, Object value) {
                extraProperties.put(key, value);
        }

        public com.decade.practice.domain.entities.Chat getChat() {
                return chat;
        }

        public void setChat(com.decade.practice.domain.entities.Chat chat) {
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

        public Collection<Edge> getEdges() {
                return edges;
        }

        public UUID getId() {
                return id;
        }

        public void setId(UUID id) {
                this.id = id;
        }

        public Receipt getReceipt() {
                return receipt;
        }

        public void setReceipt(Receipt receipt) {
                this.receipt = receipt;
        }

        public void setEdges(Collection<Edge> edges) {
                this.edges = edges;
        }

        public void setEventType(String eventType) {
                this.eventType = eventType;
        }

        public ChatIdentifier getChatIdentifier() {
                return chatIdentifier;
        }


        public void setChatIdentifier(ChatIdentifier chatIdentifier) {
                this.chatIdentifier = chatIdentifier;
        }

        public long getCreatedTime() {
                return createdTime;
        }

        public void setCreatedTime(long createdTime) {
                this.createdTime = createdTime;
        }
}
