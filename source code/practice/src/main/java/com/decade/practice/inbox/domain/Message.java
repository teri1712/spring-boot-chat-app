package com.decade.practice.inbox.domain;

import com.decade.practice.inbox.domain.events.MessageCreated;
import com.decade.practice.inbox.domain.events.MessageUpdated;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Entity
@Getter

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "message_type")
public abstract class Message extends AbstractAggregateRoot<Message> {

      @Id
      @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_sequence")
      @SequenceGenerator(name = "message_sequence", sequenceName = "message_seq", initialValue = 1000)
      private Long sequenceId;

      private UUID postingId;

      @Column(updatable = false, nullable = false)
      private UUID senderId;

      @Column(name = "message_type", insertable = false, updatable = false)
      private String messageType;

      @Column(nullable = false, updatable = false)
      @NotNull
      private String chatId;

      @Column(nullable = false, updatable = false)
      private Instant createdAt;

      private Instant updatedAt;


      @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER, mappedBy = "message")
      @MapKey(name = "senderId")
      @Getter(AccessLevel.PACKAGE)
      private Map<UUID, SeenPointer> seenPointers;

      public void deleteSeen(UUID by) {
            getSeenPointers().remove(by);
            this.onUpdated();
      }


      public Map<UUID, SeenPointer> getAllSeenPointers() {
            return Map.copyOf(seenPointers);
      }

      public void addSeen(UUID senderId, Instant at) {
            getSeenPointers().put(senderId, new SeenPointer(chatId, senderId, at, this));
            this.onUpdated();
      }

      protected Message() {
      }

      protected Message(UUID postingId, UUID senderId, String chatId, Instant createdAt, String messageType) {
            this.postingId = postingId;
            this.senderId = senderId;
            this.chatId = chatId;
            this.createdAt = createdAt;
            this.messageType = messageType;
            this.updatedAt = createdAt;
            this.seenPointers = new HashMap<>();
            this.createdAt = Instant.now();
            this.updatedAt = Instant.now();
      }

      @PrePersist
      protected void onPersisted() {
            registerEvent(new MessageCreated(sequenceId, postingId, senderId, chatId, createdAt, messageType, getState()));
      }

      private void onUpdated() {
            updatedAt = Instant.now();
            registerEvent(new MessageUpdated(sequenceId, chatId, senderId, updatedAt, getState()));
      }

      public abstract MessageState getState();

      @Entity
      @Getter
      @NoArgsConstructor
      @Table(name = "seen_pointer")
      public static class SeenPointer {

            @Id
            @GeneratedValue(strategy = GenerationType.SEQUENCE)
            private Long id;

            @Column(name = "sender_id", nullable = false)
            private UUID senderId;

            private String chatId;

            @Temporal(TemporalType.TIMESTAMP)
            private Instant at;

            private SeenPointer(String chatId, UUID senderId, Instant at, Message message) {
                  this.chatId = chatId;
                  this.senderId = senderId;
                  this.at = at;
                  this.message = message;
            }

            @ManyToOne
            @JoinColumn(name = "message_id")
            private Message message;

//
//            @PrePersist
//            void onPersisted() {
//                  log.info("Seen pointer created: {}", this);
//                  message.onUpdated();
//            }
//
//            @PreRemove
//            void onUpdated() {
//                  log.info("Seen pointer updated: {}", this);
//                  message.onUpdated();
//            }
      }
}
