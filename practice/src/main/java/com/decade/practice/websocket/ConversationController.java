package com.decade.practice.websocket;

import com.decade.practice.models.domain.TypeEvent;
import com.decade.practice.models.domain.entity.Chat;
import com.decade.practice.models.domain.entity.ChatEvent;
import com.decade.practice.models.domain.entity.User;
import com.decade.practice.usecases.CachedEntityConversationRepository;
import com.decade.practice.usecases.EventOperations;
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
        private final EventOperations eventOperations;
        private final CachedEntityConversationRepository entityRepo;

        public ConversationController(
                SimpMessagingTemplate brokerTemplate,
                EventOperations eventOperations,
                CachedEntityConversationRepository entityRepo
        ) {
                this.brokerTemplate = brokerTemplate;
                this.eventOperations = eventOperations;
                this.entityRepo = entityRepo;
        }

        @SubscribeMapping(WebSocketConfiguration.USER_QUEUE_DESTINATION)
        public ChatEvent subsSelf(User user) {
                return eventOperations.findFirstByOwnerOrderByEventVersionDesc(user);
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