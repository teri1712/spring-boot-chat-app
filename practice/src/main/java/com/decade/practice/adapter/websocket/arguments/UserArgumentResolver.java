package com.decade.practice.adapter.websocket.arguments;

import com.decade.practice.application.services.ConversationRepositoryImpl;
import com.decade.practice.domain.entities.User;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

public class UserArgumentResolver implements HandlerMethodArgumentResolver {

        private final ConversationRepositoryImpl entityRepo;

        public UserArgumentResolver(ConversationRepositoryImpl entityRepo) {
                this.entityRepo = entityRepo;
        }

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
                return User.class.isAssignableFrom(parameter.getParameterType());
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, Message<?> message) {
                var principal = SimpMessageHeaderAccessor.getUser(message.getHeaders());
                if (principal == null) {
                        throw new IllegalStateException("No principal found in message headers");
                }
                return entityRepo.getUser(principal.getName());
        }
}