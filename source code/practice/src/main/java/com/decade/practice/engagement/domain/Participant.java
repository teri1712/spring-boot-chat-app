package com.decade.practice.engagement.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
public class Participant extends AbstractAggregateRoot<Participant> {
      @EmbeddedId
      private ParticipantId participantId;

      @Embedded
      private ParticipantPolicy participantPolicy;

      @Column(nullable = false, updatable = false)
      private Instant joinDate;

      @Version
      private Integer version;

      protected Participant() {
      }

      public Participant(UUID userId, String chatId) {
            this.participantId = new ParticipantId(userId, chatId);
            this.participantPolicy = new ParticipantPolicy(true, true);
            this.joinDate = Instant.now();
      }


}
