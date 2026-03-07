package com.decade.practice.engagement.domain.events;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder

public class PreferenceChanged {

      private final Integer iconId;
      private final String roomName;
      private final String roomAvatar;
      private final String theme;

      private final UUID makerId;
      private final String chatId;

      private final Instant createdAt;

}
