package com.decade.practice.inbox.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
public class Conversation extends AbstractAggregateRoot<Conversation> {

      public static final int MAX_ROUND = 100;

      @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "conversation_seq_gen")
      @SequenceGenerator(name = "conversation_seq_gen", sequenceName = "conversation_seq")
      @Id
      private Long id;

      @Embedded
      private ConversationId conversationId;
      private Long roomId;

      @JdbcTypeCode(SqlTypes.JSON)
      @Column(columnDefinition = "jsonb")
      private List<MessageState> recents;

      private Instant modifiedAt;

      private Integer roundRobin;
      private Integer participantIndex;

      @Embedded
      private HashValue hash;

      protected Conversation() {
      }

      public Conversation(String chatId, UUID ownerId, Long roomId, Integer participantIndex) {
            this.roomId = roomId;
            this.conversationId = new ConversationId(chatId, ownerId);
            this.modifiedAt = Instant.now();
            this.participantIndex = participantIndex;
            this.roundRobin = participantIndex % MAX_ROUND;
            this.recents = new ArrayList<>();
            this.hash = new HashValue((long) conversationId.hashCode());
      }

      public void addRecent(MessageState messageState) {
            this.recents.add(0, messageState);
            this.modifiedAt = Instant.now();
            this.hash = hash.shift().plus(computeHash(messageState));
            if (this.recents.size() > 20) {
                  pop();
            }
      }

      private void pop() {
            this.recents.remove(this.recents.size() - 1);
      }

      public void updateRecent(MessageState messageState) {
            if (recents.isEmpty()) return;
            Long sequenceId = messageState.getSequenceId();
            int left = 0, right = recents.size();
            while (left < right) {
                  int mid = (left + right) / 2;
                  MessageState midState = recents.get(mid);
                  if (midState.getSequenceId() > sequenceId) {
                        left = mid + 1;
                  } else {
                        right = mid;
                  }
            }
            if (left < recents.size()) {
                  hash = hash.minus(HashValue.ONE
                            .shift(left)
                            .times(computeHash(recents.get(left))));

                  hash = hash.plus(HashValue.ONE
                            .shift(left).times(computeHash(messageState)));

                  recents.set(left, messageState);
            }
      }

      private static HashValue computeHash(MessageState messageState) {
            return new HashValue((long) messageState.getPostingId().hashCode());
      }

}


