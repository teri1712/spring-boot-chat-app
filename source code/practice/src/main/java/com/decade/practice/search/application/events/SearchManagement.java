package com.decade.practice.search.application.events;


import com.decade.practice.inbox.domain.events.TextAdded;
import com.decade.practice.search.application.ports.out.MessageDocumentRepository;
import com.decade.practice.search.application.ports.out.UserDocumentRepository;
import com.decade.practice.search.domain.MessageDocument;
import com.decade.practice.search.domain.UserDocument;
import com.decade.practice.users.domain.events.UserCreated;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SearchManagement {

      private final MessageDocumentRepository messageDocuments;
      private final UserDocumentRepository userDocuments;

      @ApplicationModuleListener
      public void on(UserCreated event) {
            log.trace("Received user: {}", event);

            UserDocument document = new UserDocument(
                      event.userId(),
                      event.username(),
                      event.name(),
                      event.gender(),
                      event.avatar());
            userDocuments.save(document);
      }


      @ApplicationModuleListener
      public void on(TextAdded event) {
            log.trace("Received currentState: {}", event);
            MessageDocument document = new MessageDocument(
                      event.postingId(),
                      event.text(),
                      event.sequenceNumber(),
                      event.chatId(),
                      event.createdAt());
            messageDocuments.save(document);
      }


}
