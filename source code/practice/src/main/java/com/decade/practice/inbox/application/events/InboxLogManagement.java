package com.decade.practice.inbox.application.events;

import com.decade.practice.inbox.application.ports.out.ConversationRepository;
import com.decade.practice.inbox.application.ports.out.DeliveryService;
import com.decade.practice.inbox.application.ports.out.LogRepository;
import com.decade.practice.inbox.domain.*;
import com.decade.practice.inbox.domain.events.InboxLogCreated;
import com.decade.practice.inbox.domain.events.MessageCreated;
import com.decade.practice.inbox.domain.events.MessageUpdated;
import com.decade.practice.inbox.dto.mapper.InboxLogMapper;
import com.decade.practice.inbox.utils.PreviewUtils;
import com.decade.practice.users.api.UserApi;
import com.decade.practice.users.api.UserInfo;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class InboxLogManagement {

      private final LogRepository logs;
      private final DeliveryService deliveryService;
      private final InboxLogMapper inboxLogMapper;
      private final ConversationRepository conversations;
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

                            MessagePreview messagePreview = new MessagePreview(message.id(), message.senderId(),
                                      PreviewUtils.getPreviewContent(conversation.getConversationId().ownerId(), message.currentState())
                                      , message.createdAt());
                            conversation.addMessagePreview(messagePreview);

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


                            conversation.setSeenBy(message.id(), message.currentState().getSeenByIds());
                            conversations.save(conversation);
                      });
      }

      @ApplicationModuleListener
      public void on(InboxLogCreated inboxLogCreated) {
            Conversation conversation = conversations.findById(new ConversationId(inboxLogCreated.chatId(), inboxLogCreated.ownerId())).orElseThrow();
            Map<UUID, UserInfo> infos = userApi.getUserInfo(Set.of(inboxLogCreated.senderId()));
            deliveryService.send(inboxLogMapper.map(inboxLogCreated, new InboxLogMapper.InboxContext(infos, conversation.getRoomName(), conversation.getRoomAvatar())));
      }
}
