package com.decade.practice.live.adapter.interceptors;

import com.decade.practice.live.application.ports.in.LiveService;
import com.decade.practice.live.domain.LiveChatId;
import com.decade.practice.live.infras.security.SocketAuthentication;
import com.decade.practice.web.security.jwt.JwtUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(0)
public class LiveInterceptor implements ChannelInterceptor {

      private final LiveService liveService;

      @Value("${websocket.topics.live}")
      private String liveTopic;

      private boolean isLiveDestination(String destination) {
            return destination != null && destination.contains(liveTopic);
      }

      private LiveChatId extractChatId(String destination) {
            return new LiveChatId(destination.substring(liveTopic.length() + 1));
      }

      @Nullable
      @Override
      public Message<?> preSend(Message<?> message, MessageChannel channel) {

            StompHeaderAccessor accessor =
                      MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
            String destination = accessor.getDestination();
            if (isLiveDestination(destination)) {
                  StompCommand command = accessor.getCommand();
                  if (command == StompCommand.SUBSCRIBE) {
                        LiveChatId chatId = extractChatId(accessor.getDestination());
                        JwtUser jwtUser = ((SocketAuthentication) accessor.getUser()).jwtUser();
                        liveService.join(chatId, jwtUser.getId());
                  } else if (command == StompCommand.UNSUBSCRIBE) {
                        LiveChatId chatId = extractChatId(accessor.getDestination());
                        JwtUser jwtUser = ((SocketAuthentication) accessor.getUser()).jwtUser();
                        liveService.leave(chatId, jwtUser.getId());
                  } else if (command == StompCommand.SEND) {
                        LiveChatId chatId = extractChatId(accessor.getDestination());
                        JwtUser jwtUser = ((SocketAuthentication) accessor.getUser()).jwtUser();
                        liveService.send(chatId, jwtUser.getId());
                        return null;
                  }
            }
            return message;
      }
}
