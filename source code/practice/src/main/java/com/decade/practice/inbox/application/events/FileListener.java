package com.decade.practice.inbox.application.events;

import com.decade.practice.inbox.application.ports.out.MessageRepository;
import com.decade.practice.inbox.domain.File;
import com.decade.practice.inbox.domain.events.FileRoomEventCreated;
import lombok.AllArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FileListener {


      private final MessageRepository messages;

      @ApplicationModuleListener
      public void on(FileRoomEventCreated eventPlaced) {
            messages.save(new File(eventPlaced.getChatEventId(), eventPlaced.getSenderId(), eventPlaced.getChatId(), eventPlaced.getCreatedAt(), eventPlaced.getFilename(), eventPlaced.getSize(), eventPlaced.getUri()));
      }

}
