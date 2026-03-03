package com.decade.practice.inbox.application.events;

import com.decade.practice.engagement.domain.events.SeenChatEventAccepted;
import com.decade.practice.inbox.application.ports.out.MessageRepository;
import com.decade.practice.inbox.domain.Message;
import com.decade.practice.inbox.domain.SeenPointer;
import lombok.AllArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@AllArgsConstructor
public class SeenListener {

      private final MessageRepository messages;

      @ApplicationModuleListener
      public void on(SeenChatEventAccepted event) {
            String chatId = event.getSnapshot().chatId();
            UUID senderId = event.getSenderId();
            Optional<Message> lastSeen = messages.findByLastSeen(chatId, senderId);
            lastSeen.ifPresent(new Consumer<Message>() {
                  @Override
                  public void accept(Message message) {
                        message.deleteSeen(senderId);
                        messages.save(message);
                  }
            });
            Optional<Message> lastMessage = messages.findFirstByChatIdOrderByCreatedAtDesc(chatId);
            lastMessage.ifPresent(new Consumer<Message>() {
                  @Override
                  public void accept(Message message) {
                        message.addSeen(new SeenPointer(chatId, senderId, event.getAt()));
                        messages.save(message);
                  }
            });
      }

}
