package com.decade.practice.inbox.application.events;

import com.decade.practice.engagement.domain.events.FileChatEventAccepted;
import com.decade.practice.inbox.application.ports.out.MessageRepository;
import com.decade.practice.inbox.domain.File;
import lombok.AllArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FileListener {


      private final MessageRepository messages;

      @ApplicationModuleListener
      public void on(FileChatEventAccepted eventPlaced) {
            messages.save(new File(eventPlaced.getChatEventId(), eventPlaced.getSenderId(), eventPlaced.getSnapshot().chatId(), eventPlaced.getCreatedAt(), eventPlaced.getFilename(), eventPlaced.getSize(), eventPlaced.getUri()));
      }

}
