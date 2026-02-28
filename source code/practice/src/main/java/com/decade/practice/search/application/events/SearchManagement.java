package com.decade.practice.search.application.events;


import com.decade.practice.engagement.dto.events.IntegrationChatSnapshot;
import com.decade.practice.engagement.dto.events.TextIntegrationChatEventPlaced;
import com.decade.practice.search.application.ports.out.MessageDocumentRepository;
import com.decade.practice.search.application.ports.out.UserDocumentRepository;
import com.decade.practice.search.domain.MessageDocument;
import com.decade.practice.search.domain.UserDocument;
import com.decade.practice.users.api.events.IntegrationUserCreated;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class SearchManagement {

      private final MessageDocumentRepository messageDocuments;
      private final UserDocumentRepository userDocuments;

      //    @KafkaListener(topics = "users.user.created", groupId = "search-service")
//    @RetryableTopic
      @ApplicationModuleListener
      public void on(IntegrationUserCreated event) {
            log.trace("Received user: {}", event);

            UserDocument document = new UserDocument(
                      event.userId(),
                      event.username(),
                      event.name(),
                      event.gender(),
                      event.avatar());
            userDocuments.save(document);
      }


      //    @KafkaListener(topics = "threads.chat-history.currentState-added", groupId = "search-service")
//    @RetryableTopic
      @ApplicationModuleListener
      public void on(TextIntegrationChatEventPlaced event) {
            log.trace("Received currentState: {}", event);
            IntegrationChatSnapshot snapshot = event.getSnapshot();
            MessageDocument document = new MessageDocument(
                      UUID.randomUUID(),
                      event.getContent(),
                      snapshot.chatId(),
                      snapshot.creators(),
                      snapshot.roomName(),
                      event.getCreatedAt());
            messageDocuments.save(document);
      }


      //    @DltHandler
//    public void handleDlt(
//            Object currentState,
//            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
//            @Header(KafkaHeaders.DLT_EXCEPTION_MESSAGE) String exceptionMessage,
//            @Header(KafkaHeaders.DLT_EXCEPTION_STACKTRACE) String stackTrace,
//            @Header(KafkaHeaders.DELIVERY_ATTEMPT) Integer attempts
//    ) {
//
//        log.error("""
//                        Message permanently failed.
//                        Original topic: {}
//                        Attempts: {}
//                        Error: {}
//                        Payload: {}
//                        Stacktrace: {}
//                        """,
//                topic,
//                attempts,
//                exceptionMessage,
//                currentState,
//                stackTrace
//        );
//    }

}
