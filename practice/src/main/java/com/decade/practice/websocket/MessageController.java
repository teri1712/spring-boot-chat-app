package com.decade.practice.websocket;

import com.decade.practice.database.repositories.EventRepository;
import com.decade.practice.entities.domain.TypeEvent;
import com.decade.practice.entities.domain.entity.Chat;
import com.decade.practice.entities.domain.entity.ChatEvent;
import com.decade.practice.entities.domain.entity.User;
import com.decade.practice.utils.ChatUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeTypeUtils;

@Controller
public class MessageController {

      private final SimpMessagingTemplate brokerTemplate;
      private final EventRepository eventRepo;
      private final WsEntityRepository entityRepo;

      public MessageController(
            SimpMessagingTemplate brokerTemplate,
            EventRepository eventRepo,
            WsEntityRepository entityRepo
      ) {
            this.brokerTemplate = brokerTemplate;
            this.eventRepo = eventRepo;
            this.entityRepo = entityRepo;
      }

      @SubscribeMapping(WsConfiguration.USER_QUEUE_DESTINATION)
      public ChatEvent subsSelf(User user) {
            return eventRepo.findFirstByOwnerOrderByEventVersionDesc(user);
      }

      private String resolveDestination(Chat chat) {
            return WsConfiguration.QUEUE_CHAT_DESTINATION + "-" + chat.getIdentifier();
      }

      @MessageMapping(WsConfiguration.TYPING_DESTINATION)
      public void pingType(
            Chat chat,
            User from,
            Message<?> message
      ) {
            SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(message);
            accessor.setContentType(MimeTypeUtils.APPLICATION_JSON);

            TypeEvent type = entityRepo.getType(chat, from, false);
            if (type != null) {
                  brokerTemplate.convertAndSend(resolveDestination(chat), type, accessor.getMessageHeaders());
            }
      }

      @SubscribeMapping(WsConfiguration.TYPING_DESTINATION)
      public TypeEvent subsType(
            Chat chat,
            User from,
            Message<?> message
      ) {
            brokerTemplate.send(resolveDestination(chat), message);
            return entityRepo.getType(chat, ChatUtils.inspectPartner(chat, from), true);
      }
}