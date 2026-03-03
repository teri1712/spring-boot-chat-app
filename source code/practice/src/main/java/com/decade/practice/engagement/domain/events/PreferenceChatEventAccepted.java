package com.decade.practice.engagement.domain.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder

public class PreferenceChatEventAccepted extends ChatEventAccepted {


      private final Integer iconId;
      private final String roomName;
      private final String roomAvatar;
      private final String theme;

}
