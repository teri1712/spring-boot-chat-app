package com.decade.practice.engagement.domain;

import com.decade.practice.engagement.domain.events.ChatCreated;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.AbstractAggregateRoot;

@Entity
@Getter
@NoArgsConstructor
public class Chat extends AbstractAggregateRoot<Chat> {

      @Id
      private String chatId;

      @Column(updatable = false)
      private Integer maxParticipants;

      @Embedded
      private ChatCreators creators;


      public Chat(String chatId, Integer maxParticipants, ChatCreators creators) {
            this.chatId = chatId;
            this.maxParticipants = maxParticipants;
            this.creators = creators;
            registerEvent(new ChatCreated(chatId, creators.getMembers(), creators.callerId()));

      }
}
