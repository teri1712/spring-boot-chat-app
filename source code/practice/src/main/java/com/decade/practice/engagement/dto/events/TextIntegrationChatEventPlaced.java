package com.decade.practice.engagement.dto.events;


import lombok.Getter;
import lombok.experimental.SuperBuilder;


@Getter
@SuperBuilder

public class TextIntegrationChatEventPlaced extends IntegrationChatEventPlaced {

      private final String content;

}
