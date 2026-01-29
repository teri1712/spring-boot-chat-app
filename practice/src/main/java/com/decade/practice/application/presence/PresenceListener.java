package com.decade.practice.application.presence;

import com.decade.practice.application.usecases.UserPresenceService;
import com.decade.practice.infra.security.jwt.JwtUserAuthentication;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Instant;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PresenceListener implements ChannelInterceptor, WebMvcConfigurer {
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

}
