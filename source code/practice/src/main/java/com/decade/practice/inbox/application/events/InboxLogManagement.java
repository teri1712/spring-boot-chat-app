package com.decade.practice.inbox.application.events;

import com.decade.practice.engagement.domain.events.PreferenceChanged;
import com.decade.practice.inbox.application.ports.out.ConversationRepository;
import com.decade.practice.inbox.application.ports.out.DeliveryService;
import com.decade.practice.inbox.application.ports.out.LogRepository;
import com.decade.practice.inbox.application.ports.out.MessageRepository;
import com.decade.practice.inbox.domain.*;
import com.decade.practice.inbox.domain.events.InboxLogCreated;
import com.decade.practice.inbox.domain.events.MessageCreated;
import com.decade.practice.inbox.domain.events.MessageUpdated;
import com.decade.practice.inbox.dto.mapper.InboxLogMapper;
import com.decade.practice.users.api.UserApi;
import com.decade.practice.users.api.UserInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class InboxLogManagement {

      private final LogRepository logs;
      private final DeliveryService deliveryService;
      private final InboxLogMapper inboxLogMapper;
      private final ConversationRepository conversations;
      private final MessageRepository messages;
      private final UserApi userApi;

      @EventListener
      public void on(MessageCreated message) {
            conversations.findByConversationId_ChatId(message.chatId())
                      .forEach(conversation -> {
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
                      .forEach(conversation -> {
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

      @ApplicationModuleListener
      public void on(InboxLogCreated inboxLogCreated) {
            Conversation conversation = conversations.findById(new ConversationId(inboxLogCreated.chatId(), inboxLogCreated.ownerId())).orElseThrow();
            Map<UUID, UserInfo> infos = userApi.getUserInfo(Set.of(inboxLogCreated.senderId()));
            deliveryService.send(inboxLogMapper.map(inboxLogCreated, new InboxLogMapper.InboxContext(inboxLogCreated.ownerId(), infos, conversation.getName(), conversation.getAvatar(), conversation.getHash().value())));
      }


      @ApplicationModuleListener
      public void on(PreferenceChanged event) {
            long rowsAffected = conversations.updateRoomNameAndRoomAvatar(event.getChatId(), event.getRoomName(), event.getRoomAvatar());
            log.info("Updated {} rows for chat {}", rowsAffected, event.getChatId());


            messages.save(new Preference(
                      UUID.randomUUID(),
                      event.getMakerId(),
                      event.getChatId(),
                      event.getCreatedAt(),
                      event.getIconId(),
                      event.getRoomAvatar(),
                      event.getRoomName(),
                      event.getTheme()));
      }
}
