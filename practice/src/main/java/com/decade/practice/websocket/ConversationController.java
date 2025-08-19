package com.decade.practice.websocket;

import com.decade.practice.data.repositories.EventRepository;
import com.decade.practice.model.domain.TypeEvent;
import com.decade.practice.model.domain.entity.Chat;
import com.decade.practice.model.domain.entity.ChatEvent;
import com.decade.practice.model.domain.entity.User;
import com.decade.practice.utils.ChatUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeTypeUtils;

@Controller
public class ConversationController {

        private final SimpMessagingTemplate brokerTemplate;
        private final EventRepository eventRepo;
        private final CachedEntityConversationRepository entityRepo;

        public ConversationController(
                SimpMessagingTemplate brokerTemplate,
                EventRepository eventRepo,
                CachedEntityConversationRepository entityRepo
        ) {
                this.brokerTemplate = brokerTemplate;
                this.eventRepo = eventRepo;
                this.entityRepo = entityRepo;
        }

        @SubscribeMapping(WebSocketConfiguration.USER_QUEUE_DESTINATION)
        public ChatEvent subsSelf(User user) {
                return eventRepo.findFirstByOwnerOrderByEventVersionDesc(user);
        }

        private String resolveDestination(Chat chat) {
                return WebSocketConfiguration.CHAT_DESTINATION + "-" + chat.getIdentifier();
        }

        @MessageMapping(WebSocketConfiguration.TYPING_DESTINATION)
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

        @SubscribeMapping(WebSocketConfiguration.TYPING_DESTINATION)
        public TypeEvent subsType(
                Chat chat,
                User from,
                Message<?> message
        ) {
                brokerTemplate.send(resolveDestination(chat), message);
                return entityRepo.getType(chat, ChatUtils.inspectPartner(chat, from), true);
        }
}