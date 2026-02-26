package com.decade.practice.inbox.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class SeenPointer {

      @Id
      @GeneratedValue(strategy = GenerationType.SEQUENCE)
      private Long id;

      @Column(name = "sender_id")
      private UUID senderId;

      private String chatId;

      @Temporal(TemporalType.TIMESTAMP)
      private Instant at;

      public SeenPointer(String chatId, UUID senderId, Instant at) {
            this.chatId = chatId;
            this.senderId = senderId;
            this.at = at;
      }


      @Column(name = "message_id")
      @Setter
      private Long messageId;
}