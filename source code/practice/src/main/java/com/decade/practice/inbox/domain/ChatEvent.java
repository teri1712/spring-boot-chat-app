package com.decade.practice.inbox.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "receipt_type")
public abstract class ChatEvent extends AbstractAggregateRoot<ChatEvent> {

      @Id
      private UUID id;

      private String chatId;
      private UUID senderId;
      private Instant createdAt;

      public ChatEvent(UUID id, String chatId, UUID senderId) {
            this.id = id;
            this.chatId = chatId;
            this.senderId = senderId;
            this.createdAt = Instant.now();
            onCreated();
      }

      @Version
      private Integer version;

      private Integer eventVersion = 0;

      protected ChatEvent() {

      }

      protected abstract void onCreated();

}
