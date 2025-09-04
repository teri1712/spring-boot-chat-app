package com.decade.practice.websocket.arguments;

import com.decade.practice.models.domain.entity.Chat;
import com.decade.practice.usecases.CachedEntityConversationRepository;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;

public class ChatArgumentResolver implements HandlerMethodArgumentResolver {

        private final CachedEntityConversationRepository entityRepo;

        public ChatArgumentResolver(CachedEntityConversationRepository entityRepo) {
                this.entityRepo = entityRepo;
        }

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
                return Chat.class.isAssignableFrom(parameter.getParameterType());
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, Message<?> message) {
                var id = ChatIdentifierArgumentResolver.resolveChatHeader(message);
                return entityRepo.getChat(id);
        }
}