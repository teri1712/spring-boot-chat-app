package com.decade.practice.inbox.domain;

import jakarta.annotation.Nullable;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
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

      @NotNull
      private Integer iconId;

      @Nullable
      private String roomName;

      @Nullable
      private String roomAvatar;

      @Nullable
      private String theme;

      public Preference(UUID chatEventId, UUID senderId, String chatId, Instant createdAt, Integer iconId, @Nullable String roomAvatar, @Nullable String roomName, @Nullable String theme) {
            super(chatEventId, senderId, chatId, createdAt, PREFERENCE_TYPE);
            this.iconId = iconId;
            this.roomAvatar = roomAvatar;
            this.roomName = roomName;
            this.theme = theme;
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
                      .iconId(getIconId())
                      .theme(getTheme())
                      .roomName(getRoomName())
                      .roomAvatar(getRoomAvatar())
                      .build();
      }
}
