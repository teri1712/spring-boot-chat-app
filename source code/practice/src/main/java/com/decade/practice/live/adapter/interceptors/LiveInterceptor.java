package com.decade.practice.live.adapter.interceptors;

import com.decade.practice.live.application.ports.in.LiveService;
import com.decade.practice.live.infras.security.SocketAuthentication;
import com.decade.practice.shared.security.jwt.JwtUser;
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

    @Value("${websocket.topics.room}")
    private String roomTopic;

    private boolean isRoomDestination(String destination) {
        return destination != null && destination.contains(roomTopic);
    }

    private String extractChatId(String destination) {
        return destination.substring(roomTopic.length() + 1);
    }

    @Nullable
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor =
            MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        String destination = accessor.getDestination();
        if (isRoomDestination(destination)) {
            StompCommand command = accessor.getCommand();
            if (command == StompCommand.SUBSCRIBE) {
                String chatId = extractChatId(accessor.getDestination());
                JwtUser jwtUser = ((SocketAuthentication) accessor.getUser()).jwtUser();
                liveService.join(chatId, jwtUser.getId(), jwtUser.getClaims().avatar());
            } else if (command == StompCommand.UNSUBSCRIBE) {
                String chatId = extractChatId(accessor.getDestination());
                JwtUser jwtUser = ((SocketAuthentication) accessor.getUser()).jwtUser();
                liveService.leave(chatId, jwtUser.getId(), jwtUser.getClaims().avatar());
            } else if (command == StompCommand.SEND) {
                String chatId = extractChatId(accessor.getDestination());
                JwtUser jwtUser = ((SocketAuthentication) accessor.getUser()).jwtUser();
                liveService.send(chatId, jwtUser.getId(), jwtUser.getClaims().avatar());
                return null;
            }
        }
        return message;
    }
}
