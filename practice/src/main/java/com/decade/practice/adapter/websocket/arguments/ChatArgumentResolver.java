package com.decade.practice.adapter.websocket.arguments;

import com.decade.practice.application.services.ConversationRepositoryImpl;
import com.decade.practice.domain.entities.Chat;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;

public class ChatArgumentResolver implements HandlerMethodArgumentResolver {

        private final ConversationRepositoryImpl entityRepo;

        public ChatArgumentResolver(ConversationRepositoryImpl entityRepo) {
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