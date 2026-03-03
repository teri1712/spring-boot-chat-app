package com.decade.practice.inbox.domain;

import com.decade.practice.inbox.apis.events.ConversationCreated;
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

      private String roomName;
      private String roomAvatar;

      @Version
      private Integer version;

      @JdbcTypeCode(SqlTypes.JSON)
      @Column(columnDefinition = "jsonb")
      private List<MessagePreview> previews;

      private Instant modifiedAt;

      @Embedded
      private HashValue hash;

      protected Conversation() {
      }

      public Conversation(String chatId, UUID ownerId, String roomName, String roomAvatar) {
            this.conversationId = new ConversationId(chatId, ownerId);
            this.previews = new ArrayList<>();
            this.roomName = roomName;
            this.roomAvatar = roomAvatar;
            this.hash = new HashValue((long) conversationId.hashCode());
            registerEvent(new ConversationCreated(chatId, ownerId, roomName));
      }

      public void addPreview(MessagePreview messagePreview) {
            this.previews.add(0, messagePreview);
            this.modifiedAt = Instant.now();
            this.hash = hash.plus(computeHash(messagePreview));
            if (this.previews.size() > 20) {
                  pop();
            }
      }

      private void pop() {
            this.previews.remove(this.previews.size() - 1);
      }


      public void update(String roomName, String roomAvatar) {
            this.roomName = roomName;
            this.roomAvatar = roomAvatar;
      }

      public void updatePreview(MessagePreview preview) {
            if (previews.isEmpty()) return;
            MessageState latest = previews.get(0).messageState();
            if (Objects.equals(latest.getSequenceId(), preview.messageState().getSequenceId()))
                  previews.set(0, preview);
      }

      private static HashValue computeHash(MessagePreview preview) {
            MessageState messageState = preview.messageState();
            HashValue hashValue = new HashValue(messageState.getCreatedAt().toEpochMilli());
            hashValue = hashValue.plus(new HashValue((long) preview.displayContent().hashCode()));
            return hashValue;
      }

}


