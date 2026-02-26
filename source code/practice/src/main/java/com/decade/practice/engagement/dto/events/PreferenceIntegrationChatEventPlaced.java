package com.decade.practice.engagement.dto.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder

public class PreferenceIntegrationChatEventPlaced extends IntegrationChatEventPlaced {


      private final Integer iconId;
      private final String roomName;
      private final String roomAvatar;
      private final String theme;

}
