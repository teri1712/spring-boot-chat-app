package com.decade.practice.application.presence;

import com.decade.practice.application.usecases.UserPresenceService;
import com.decade.practice.infra.security.jwt.JwtUserAuthentication;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

import java.time.Instant;
import java.util.Map;

@Component
public class PresenceListener implements ChannelInterceptor, HandshakeInterceptor, WebMvcConfigurer {
    private final UserPresenceService userPresenceService;

    public PresenceListener(UserPresenceService userPresenceService) {
        this.userPresenceService = userPresenceService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                if (request.getUserPrincipal() != null && request.getUserPrincipal() instanceof JwtUserAuthentication authentication) {
                    userPresenceService.set(authentication.getPrincipal(), Instant.now());
                }
                return true;
            }
        });
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        var principal = SimpMessageHeaderAccessor.getUser(message.getHeaders());
        if (principal instanceof JwtUserAuthentication authentication) {
            userPresenceService.set(authentication.getPrincipal(), Instant.now());
        }
        return message;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        if (request.getPrincipal() != null && request.getPrincipal() instanceof JwtUserAuthentication authentication) {
            userPresenceService.set(authentication.getPrincipal(), Instant.now());
        }
    }
}
