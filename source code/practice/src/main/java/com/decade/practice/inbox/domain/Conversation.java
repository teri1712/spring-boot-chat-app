package com.decade.practice.inbox.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
public class Conversation extends AbstractAggregateRoot<Conversation> {

      @EmbeddedId
      private ConversationId conversationId;

      private String name;
      private String avatar;

      @Version
      private Integer version;

      @JdbcTypeCode(SqlTypes.JSON)
      @Column(columnDefinition = "jsonb")
      private List<MessageState> recents;

      private Instant modifiedAt;

      @Embedded
      private HashValue hash;

      protected Conversation() {
      }

      public Conversation(String chatId, UUID ownerId, String name, String avatar) {
            this.conversationId = new ConversationId(chatId, ownerId);
            this.recents = new ArrayList<>();
            this.name = name;
            this.avatar = avatar;
            this.hash = new HashValue((long) conversationId.hashCode());
      }

      public void addRecent(MessageState messageState) {
            this.recents.add(0, messageState);
            this.modifiedAt = Instant.now();
            this.hash = hash.plus(computeHash(messageState));
            if (this.recents.size() > 20) {
                  pop();
            }
      }

      private void pop() {
            this.recents.remove(this.recents.size() - 1);
      }


      public void update(String roomName, String roomAvatar) {
            this.name = roomName;
            this.avatar = roomAvatar;
      }

      public void updateRecent(MessageState messageState) {
            if (recents.isEmpty()) return;
            MessageState latest = recents.get(0);
            if (Objects.equals(latest.getSequenceId(), messageState.getSequenceId()))
                  recents.set(0, messageState);
      }

      private static HashValue computeHash(MessageState messageState) {
            HashValue hashValue = new HashValue(messageState.getCreatedAt().toEpochMilli());
            hashValue = hashValue.plus(new HashValue((long) messageState.getChatEventId().hashCode()));
            return hashValue;
      }

}


