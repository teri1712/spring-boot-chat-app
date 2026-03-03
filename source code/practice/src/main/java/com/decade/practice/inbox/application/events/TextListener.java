package com.decade.practice.inbox.application.events;

import com.decade.practice.engagement.domain.events.TextChatEventAccepted;
import com.decade.practice.inbox.application.ports.out.MessageRepository;
import com.decade.practice.inbox.domain.Text;
import lombok.AllArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TextListener {

      private final MessageRepository messages;

      @ApplicationModuleListener
      public void on(TextChatEventAccepted event) {
            messages.save(new Text(event.getChatEventId(), event.getSenderId(), event.getSnapshot().chatId(), event.getCreatedAt(), event.getContent()));
      }


}
