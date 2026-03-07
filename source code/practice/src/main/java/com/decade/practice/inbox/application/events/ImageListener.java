package com.decade.practice.inbox.application.events;

import com.decade.practice.inbox.application.ports.out.MessageRepository;
import com.decade.practice.inbox.domain.Image;
import com.decade.practice.inbox.domain.ImageSpec;
import com.decade.practice.inbox.domain.events.ImageChatEventCreated;
import lombok.AllArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ImageListener {

      private final MessageRepository messages;

      @ApplicationModuleListener
      public void on(ImageChatEventCreated eventPlaced) {
            messages.save(new Image(eventPlaced.getChatEventId(), eventPlaced.getSenderId(), eventPlaced.getChatId(), eventPlaced.getCreatedAt(),
                      new ImageSpec(eventPlaced.getUri(), eventPlaced.getFilename(), eventPlaced.getWidth(), eventPlaced.getHeight(), eventPlaced.getFormat())
            ));
      }


}
