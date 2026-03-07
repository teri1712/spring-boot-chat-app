package com.decade.practice.inbox.domain.events;


import lombok.Getter;
import lombok.experimental.SuperBuilder;


@Getter
@SuperBuilder

public class TextChatEventCreated extends ChatEventCreated {

      private final String content;

}
