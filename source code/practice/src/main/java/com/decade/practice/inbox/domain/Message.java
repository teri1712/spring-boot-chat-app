package com.decade.practice.inbox.domain;

import com.decade.practice.inbox.domain.events.MessageCreated;
import com.decade.practice.inbox.domain.events.MessageUpdated;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Getter

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "message_type")
public abstract class Message extends AbstractAggregateRoot<Message> {

      @Id
      @GeneratedValue(strategy = GenerationType.SEQUENCE)
      private Long sequenceId;

      private UUID chatEventId;

      @Column(updatable = false, nullable = false)
      private UUID senderId;

      @Column(name = "message_type", insertable = false, updatable = false)
      private String messageType;

      @Column(nullable = false, updatable = false)
      @NotNull
      private String chatId;

      @Temporal(TemporalType.TIMESTAMP)
      private Instant createdAt;
      
      private Instant updatedAt;


      @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
      @JoinColumn(name = "message_id")
      @MapKey(name = "senderId")
      private Map<UUID, SeenPointer> seenPointers;

      public void deleteSeen(UUID by) {
            seenPointers.remove(by);
            updatedAt = Instant.now();
            registerEvent(new MessageUpdated(sequenceId, chatId, senderId, updatedAt, getState()));
      }

      public Map<UUID, SeenPointer> getSeenPointers() {
            return Map.copyOf(seenPointers);
      }

      public void addSeen(SeenPointer seenPointer) {
            seenPointers.put(seenPointer.getSenderId(), seenPointer);
            updatedAt = Instant.now();
            registerEvent(new MessageUpdated(sequenceId, chatId, senderId, updatedAt, getState()));
      }

      protected Message() {
      }

      public Message(UUID chatEventId, UUID senderId, String chatId, Instant createdAt, String messageType) {
            this.chatEventId = chatEventId;
            this.senderId = senderId;
            this.chatId = chatId;
            this.createdAt = createdAt;
            this.messageType = messageType;
            this.updatedAt = createdAt;
            this.seenPointers = new HashMap<>();
      }

      @PrePersist
      void onPersisted() {
            assert sequenceId != null;
            registerEvent(new MessageCreated(sequenceId, chatEventId, senderId, chatId, createdAt, messageType, getState()));
      }

      public abstract MessageState getState();
}
