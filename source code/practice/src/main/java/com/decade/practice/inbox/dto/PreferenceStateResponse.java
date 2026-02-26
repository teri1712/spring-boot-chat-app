package com.decade.practice.inbox.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@SuperBuilder

@Jacksonized
public class PreferenceStateResponse extends MessageStateResponse {
      private final Integer iconId;
      private final String roomName;
      private final String roomAvatar;
      private final String theme;

}
