package com.decade.practice.inbox.domain;

import com.decade.practice.inbox.apis.events.ConversationCreated;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.Instant;
import java.util.*;

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
      private List<MessagePreview> messagePreviews;


      @JdbcTypeCode(SqlTypes.JSON)
      @Column(columnDefinition = "jsonb")
      private Set<UUID> seenBy;

      private Instant modifiedAt;

      @Embedded
      private HashValue hash;

      protected Conversation() {
      }

      public Conversation(String chatId, UUID ownerId, String roomName, String roomAvatar) {
            this.conversationId = new ConversationId(chatId, ownerId);
            this.messagePreviews = new ArrayList<>();
            this.roomName = roomName;
            this.roomAvatar = roomAvatar;
            this.hash = new HashValue((long) conversationId.hashCode());
            registerEvent(new ConversationCreated(chatId, ownerId, roomName));
      }

      public void addMessagePreview(MessagePreview messagePreview) {
            this.messagePreviews.add(0, messagePreview);
            this.modifiedAt = messagePreview.createdAt();
            this.hash = hash.plus(computeHash(messagePreview));
            if (this.messagePreviews.size() > 20) {
                  pop();
            }
            this.seenBy = new HashSet<>();
      }

      private void pop() {
            this.messagePreviews.remove(this.messagePreviews.size() - 1);
      }


      public void update(String roomName, String roomAvatar) {
            this.roomName = roomName;
            this.roomAvatar = roomAvatar;
      }

      public void setSeenBy(Long messageId, Set<UUID> seenBy) {
            if (Objects.equals(messagePreviews.get(0).id(), messageId))
                  this.seenBy = new HashSet<>(seenBy);
      }

      private static HashValue computeHash(MessagePreview messagePreview) {
            HashValue hashValue = new HashValue(messagePreview.createdAt().toEpochMilli());
            for (int i = 0; i < messagePreview.content().length(); i++) {
                  char c = messagePreview.content().charAt(i);
                  hashValue = hashValue.plus(new HashValue((long) c));
            }
            return hashValue;
      }

}


