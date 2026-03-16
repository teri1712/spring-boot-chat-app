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
public abstract class RoomEvent extends AbstractAggregateRoot<RoomEvent> {

      @Id
      @Column(name = "id")
      private UUID postingId;

      private String chatId;
      private UUID senderId;
      private Instant createdAt;

      public RoomEvent(UUID postingId, String chatId, UUID senderId) {
            this.postingId = postingId;
            this.chatId = chatId;
            this.senderId = senderId;
            this.createdAt = Instant.now();
      }

      @Version
      private Integer version;

      private Integer eventVersion = 0;

      protected RoomEvent() {

      }

      @PrePersist
      protected abstract void onCreated();

}
