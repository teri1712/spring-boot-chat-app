package com.decade.practice.inbox.domain;

import com.decade.practice.inbox.domain.events.RoomCreated;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Room extends AbstractAggregateRoot<Room> {
      @Id
      private String chatId;

      @Embedded
      private RoomInfo info;

      private UUID creator;

      private Integer participantCount;

      private Instant lastActivity;

      @Version
      private Integer version;

      public Room(String chatId, UUID creator, String name, String avatar, Set<UUID> participants) {
            this.chatId = chatId;
            this.info = new RoomInfo(name, avatar);
            this.creator = creator;
            this.lastActivity = Instant.now();
            this.representatives = new HashSet<>();
            this.participantCount = participants.size();
            participants.forEach(this::addRepresentative);
            registerEvent(new RoomCreated(chatId, Set.copyOf(representatives)));
      }

      @JdbcTypeCode(SqlTypes.JSON)
      @Column(columnDefinition = "jsonb")
      private Set<UUID> representatives;

      public void addRepresentative(UUID representative) {
            if (representatives.size() > 10)
                  return;
            representatives.add(representative);
      }

      public void removeRepresentative(UUID representative) {
            representatives.remove(representative);
      }

      public void update(String name, String avatar) {
            this.info = new RoomInfo(name, avatar);
      }

      public void incParticipantCount() {
            participantCount++;
      }

      public void decParticipantCount() {
            participantCount--;
      }

      public void refreshLastActivity() {
            this.lastActivity = Instant.now();
      }
}
