package com.decade.practice.search.application.events;


import com.decade.practice.search.application.ports.out.ChatDocumentRepository;
import com.decade.practice.search.application.ports.out.MessageDocumentRepository;
import com.decade.practice.search.application.ports.out.UserDocumentRepository;
import com.decade.practice.search.domain.ChatDocument;
import com.decade.practice.search.domain.MessageDocument;
import com.decade.practice.search.domain.UserDocument;
import com.decade.practice.threads.api.events.ChatHistoryCreated;
import com.decade.practice.threads.api.events.HistoryMessageAdded;
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

    private final ChatDocumentRepository chatDocuments;
    private final MessageDocumentRepository messageDocuments;
    private final UserDocumentRepository userDocuments;

    //    @KafkaListener(topics = "users.user.created", groupId = "search-service")
//    @RetryableTopic
    @ApplicationModuleListener
    public void onAccountEvent(IntegrationUserCreated event) {
        log.trace("Received user: {}", event);

        UserDocument document = new UserDocument(event.userId(), event.username(), event.name(), event.gender(), event.avatar());
        userDocuments.save(document);
    }


    //    @KafkaListener(topics = "threads.chat-history.message-added", groupId = "search-service")
//    @RetryableTopic
    @ApplicationModuleListener
    public void onMessageEvent(HistoryMessageAdded event) {
        log.trace("Received message: {}", event);

        ChatDocument chatDocument = chatDocuments.findById(ChatDocument.getKey(event.chatId(), event.ownerId()))
                .orElse(new ChatDocument(event.chatId(), event.ownerId(), event.roomName()));

        String roomName = event.roomName();
        chatDocument.setRoomName(roomName);
        MessageDocument document = new MessageDocument(UUID.randomUUID(), event.ownerId(), event.chatId(), roomName, event.message(), event.createdAt());
        messageDocuments.save(document);
    }

    //    @KafkaListener(topics = "threads.chat-history.created", groupId = "search-service")
//    @RetryableTopic
    @ApplicationModuleListener
    public void onMessageEvent(ChatHistoryCreated event) {
        log.trace("Received chat: {}", event);

        ChatDocument document = chatDocuments.findById(ChatDocument.getKey(event.chatId(), event.ownerId()))
                .orElse(new ChatDocument(event.chatId(), event.ownerId(), event.roomName()));
        document.setRoomName(event.roomName());
        chatDocuments.save(document);
    }

    //    @DltHandler
//    public void handleDlt(
//            Object event,
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
//                event,
//                stackTrace
//        );
//    }

}
