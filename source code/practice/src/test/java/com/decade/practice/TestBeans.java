package com.decade.practice;

import com.decade.practice.engagement.domain.ChatCreators;
import com.decade.practice.engagement.domain.events.ChatSnapshot;
import com.decade.practice.engagement.domain.events.TextChatEventAccepted;
import com.decade.practice.engagement.domain.services.TwoParticipantChatIdentifierMaker;
import lombok.AllArgsConstructor;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Stream;

@TestConfiguration
public class TestBeans {

      @Bean
      TaskExecutor taskExecutor() {
            return new SyncTaskExecutor();
      }

      @TestComponent
      @AllArgsConstructor
      public static class PrivateChatSender {

            private final TransactionalApplicationEventPublisher applicationEventPublisher;

            public void sendPrivateText(String message, UUID senderId, UUID recipientId) {
                  String chatId = new TwoParticipantChatIdentifierMaker().make(new ChatCreators(senderId, recipientId));

                  applicationEventPublisher.publishEvent(TextChatEventAccepted.builder()
                            .senderId(senderId)
                            .createdAt(Instant.now())
                            .snapshot(new ChatSnapshot(chatId, null, null, Stream.of(senderId, recipientId).distinct().toList(), Stream.of(senderId, recipientId).distinct().toList()))
                            .content(message)
                            .build()
                  );
            }

      }

      @AllArgsConstructor
      @TestComponent
      public static class TransactionalApplicationEventPublisher {

            private final ApplicationEventPublisher applicationEventPublisher;

            @Transactional(propagation = Propagation.REQUIRES_NEW)
            public void publishEvent(Object event) {
                  applicationEventPublisher.publishEvent(event);
            }

      }
}
