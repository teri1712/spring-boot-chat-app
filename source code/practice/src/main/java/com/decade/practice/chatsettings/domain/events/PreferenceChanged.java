package com.decade.practice.chatsettings.domain.events;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder

public class PreferenceChanged {

      private final Integer iconId;
      private final String customName;
      private final String customAvatar;
      private final String theme;

      private final UUID makerId;
      private final String chatId;

      private final Instant createdAt;

}
