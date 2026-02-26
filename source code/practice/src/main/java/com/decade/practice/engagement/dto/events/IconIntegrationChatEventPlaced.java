package com.decade.practice.engagement.dto.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class IconIntegrationChatEventPlaced extends IntegrationChatEventPlaced {


      private final Integer iconId;

}
