package com.decade.practice.inbox.domain;

import com.decade.practice.inbox.domain.events.InboxLogCreated;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class InboxLog extends AbstractAggregateRoot<InboxLog> {

      @Id
      @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inbox_log_sequence")
      @SequenceGenerator(name = "inbox_log_sequence", sequenceName = "inbox_log_seq", initialValue = 1000)
      private Long sequenceId;

      private String chatId;
      private UUID senderId;
      private UUID ownerId;
      private Long messageId;

      public InboxLog(LogAction action, String chatId, UUID senderId, UUID ownerId, Long messageId, MessageState messageState) {
            this.action = action;
            this.chatId = chatId;
            this.senderId = senderId;
            this.messageId = messageId;
            this.messageState = messageState;
            this.ownerId = ownerId;
      }

      @PrePersist
      void onPersisted() {
            registerEvent(new InboxLogCreated(sequenceId, chatId, messageId, senderId, ownerId, action, getMessageState()));
      }

      @Enumerated(EnumType.STRING)
      private LogAction action;


      @JdbcTypeCode(SqlTypes.JSON)
      @Column(columnDefinition = "jsonb")
      private MessageState messageState;

}
