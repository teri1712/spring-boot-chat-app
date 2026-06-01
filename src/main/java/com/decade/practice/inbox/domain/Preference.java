package com.decade.practice.inbox.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

import static com.decade.practice.inbox.domain.Preference.PREFERENCE_TYPE;

@Getter
@Entity
@DiscriminatorValue(PREFERENCE_TYPE)
@NoArgsConstructor
public class Preference extends Message {

      public Preference(UUID chatEventId, UUID senderId, String chatId, Instant createdAt) {
            super(chatEventId, senderId, chatId, createdAt, PREFERENCE_TYPE);
      }

      public static final String PREFERENCE_TYPE = "PREFERENCE";

      @Override
      public MessageState getState() {
            return PreferenceState.builder()
                      .sequenceId(getSequenceId())
                      .postingId(getPostingId())
                      .senderId(getSenderId())
                      .messageType(getMessageType())
                      .chatId(getChatId())
                      .createdAt(getCreatedAt())
                      .seenByIds(getAllSeenPointers().keySet())
                      .build();
      }
}
