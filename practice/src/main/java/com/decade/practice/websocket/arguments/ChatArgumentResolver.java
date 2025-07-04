package com.decade.practice.websocket.arguments;

import com.decade.practice.model.domain.entity.Chat;
import com.decade.practice.websocket.WsEntityRepository;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;

public class ChatArgumentResolver implements HandlerMethodArgumentResolver {

      private final WsEntityRepository entityRepo;

      public ChatArgumentResolver(WsEntityRepository entityRepo) {
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