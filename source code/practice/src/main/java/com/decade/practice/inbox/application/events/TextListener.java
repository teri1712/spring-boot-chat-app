package com.decade.practice.inbox.application.events;

import com.decade.practice.inbox.application.ports.out.MessageRepository;
import com.decade.practice.inbox.domain.Text;
import com.decade.practice.inbox.domain.events.TextChatEventCreated;
import lombok.AllArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TextListener {

      private final MessageRepository messages;

      @ApplicationModuleListener
      public void on(TextChatEventCreated event) {
            messages.save(new Text(event.getChatEventId(), event.getSenderId(), event.getChatId(), event.getCreatedAt(), event.getContent()));
      }


}
