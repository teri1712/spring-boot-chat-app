package com.decade.practice.engagement.domain.events;


import lombok.Getter;
import lombok.experimental.SuperBuilder;


@Getter
@SuperBuilder

public class TextChatEventAccepted extends ChatEventAccepted {

      private final String content;

}
