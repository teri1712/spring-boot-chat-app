package com.decade.practice.inbox.application.events;

import com.decade.practice.inbox.application.ports.out.*;
import com.decade.practice.inbox.domain.*;
import com.decade.practice.inbox.domain.events.InboxLogCreated;
import com.decade.practice.inbox.domain.events.MessageCreated;
import com.decade.practice.inbox.domain.events.MessageUpdated;
import com.decade.practice.inbox.domain.services.ConversationInfoService;
import com.decade.practice.inbox.dto.mapper.InboxLogMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class InboxLogManagement {

      private final LogRepository logs;
      private final DeliveryService deliveryService;
      private final InboxLogMapper inboxLogMapper;
      private final ConversationRepository conversations;
      private final RoomRepository rooms;
      private final MessageRepository messages;
      private final ConversationInfoService conversationInfoService;
      private final UserLookUp userLookUp;

      @EventListener
      public void on(MessageCreated message) {
            conversations.findByConversationId_ChatId(message.chatId()).forEach(conversationView -> {
                  Conversation conversation = conversationView.conversation();
                  InboxLog log = new InboxLog(LogAction.ADDITION,
                            message.chatId(),
                            message.senderId(),
                            conversation.getConversationId().ownerId(),
                            message.id(),
                            message.currentState());
                  logs.save(log);

                  conversation.addRecent(message.currentState());

                  conversations.save(conversation);

            });
      }

      @EventListener
      public void on(MessageUpdated message) {
            conversations.findByConversationId_ChatId(message.chatId())
                      .forEach(conversationView -> {
                            Conversation conversation = conversationView.conversation();
                            InboxLog log = new InboxLog(LogAction.UPDATE,
                                      message.chatId(),
                                      message.senderId(),
                                      conversation.getConversationId().ownerId(),
                                      message.id(),
                                      message.currentState());
                            logs.save(log);

                            conversation.updateRecent(message.currentState());
                            conversations.save(conversation);
                      });
      }

      @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
      public void on(InboxLogCreated inboxLogCreated) {
            UUID ownerId = inboxLogCreated.ownerId();
            String chatId = inboxLogCreated.chatId();
            Conversation conversation = conversations.findById(new ConversationId(chatId, ownerId)).orElseThrow();
            Room room = rooms.findById(chatId).orElseThrow();
            Message message = messages.findById(inboxLogCreated.messageId()).orElseThrow();
            deliveryService.send(inboxLogMapper.map(inboxLogCreated, message.getPostingId(), userLookUp, conversation, conversationInfoService.getInfo(ownerId, room)));
      }


}
