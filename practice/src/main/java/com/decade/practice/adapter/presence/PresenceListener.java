package com.decade.practice.adapter.presence;

import com.decade.practice.application.usecases.ConversationRepository;
import com.decade.practice.application.usecases.UserPresenceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@ConditionalOnBean({UserPresenceService.class, ConversationRepository.class})
public class PresenceListener implements ChannelInterceptor, HandshakeInterceptor, WebMvcConfigurer {
        private final UserPresenceService userPresenceService;
        private final ConversationRepository conversationRepository;

        public PresenceListener(UserPresenceService userPresenceService, ConversationRepository conversationRepository) {
                this.userPresenceService = userPresenceService;
                this.conversationRepository = conversationRepository;
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new HandlerInterceptor() {
                        @Override
                        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                                try {
                                        userPresenceService.set(request.getUserPrincipal().getName());
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                                return true;
                        }
                });
        }

        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
                var principal = SimpMessageHeaderAccessor.getUser(message.getHeaders());
                if (principal == null) {
                        return message;
                }
                userPresenceService.set(conversationRepository.getUser(principal.getName()));
                return message;
        }

        @Override
        public boolean beforeHandshake(
                ServerHttpRequest request,
                ServerHttpResponse response,
                WebSocketHandler wsHandler,
                Map<String, Object> attributes
        ) {
                return true;
        }

        @Override
        public void afterHandshake(
                ServerHttpRequest request,
                ServerHttpResponse response,
                WebSocketHandler wsHandler,
                Exception exception
        ) {
                String username = request.getPrincipal().getName();
                userPresenceService.set(conversationRepository.getUser(username));
        }
}
