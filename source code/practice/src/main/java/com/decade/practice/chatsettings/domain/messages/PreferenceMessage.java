package com.decade.practice.chatsettings.domain.messages;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder

public class PreferenceMessage {

      private final Integer iconId;
      private final String customName;
      private final String customAvatar;
      private final String themeBackground;
      private final String themeName;


}
