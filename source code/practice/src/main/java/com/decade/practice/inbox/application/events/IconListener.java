package com.decade.practice.inbox.application.events;

import com.decade.practice.engagement.dto.events.IconIntegrationChatEventPlaced;
import com.decade.practice.inbox.application.ports.out.MessageRepository;
import com.decade.practice.inbox.domain.Icon;
import lombok.AllArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class IconListener {

      private final MessageRepository messages;

      @ApplicationModuleListener
      public void on(IconIntegrationChatEventPlaced eventPlaced) {
            messages.save(new Icon(eventPlaced.getChatEventId(), eventPlaced.getSenderId(), eventPlaced.getSnapshot().chatId(), eventPlaced.getCreatedAt(), eventPlaced.getIconId()));
      }
}
