package com.decade.practice.web;

import com.decade.practice.shared.security.jwt.JwtUserAuthentication;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@AllArgsConstructor
public class ConnectionGateway implements ChannelInterceptor, WebMvcConfigurer, HandshakeInterceptor {

    private final ConnectionInteractionService interactionService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                String ip = request.getRemoteAddr();
                String userAgent = request.getHeader("User-Agent");

                if (request.getUserPrincipal() != null && request.getUserPrincipal() instanceof JwtUserAuthentication authentication) {
                    UUID userId = authentication.getPrincipal().getClaims().id();
                    interactionService.publish(userId, ip, userAgent);
                }
                return true;
            }
        });
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        var principal = SimpMessageHeaderAccessor.getUser(message.getHeaders());

        SimpMessageHeaderAccessor accessor =
            MessageHeaderAccessor.getAccessor(message, SimpMessageHeaderAccessor.class);
        Map<String, Object> attrs = accessor.getSessionAttributes();

        String ip = attrs == null ? null : (String) attrs.get("ip");
        String userAgent = attrs == null ? null : (String) attrs.get("userAgent");
        if (principal instanceof JwtUserAuthentication authentication && attrs != null) {
            UUID userId = authentication.getPrincipal().getClaims().id();
            interactionService.publish(userId, ip, userAgent);
        }
        return message;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String ip = request.getRemoteAddress().getAddress().getHostAddress();
        String userAgent = request.getHeaders().getFirst("User-Agent");
        attributes.put("ip", ip);
        attributes.put("userAgent", userAgent);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, @Nullable Exception exception) {

    }
}
