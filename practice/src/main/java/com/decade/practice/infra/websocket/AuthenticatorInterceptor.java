package com.decade.practice.infra.websocket;

import com.decade.practice.infra.security.UserClaimsService;
import com.decade.practice.infra.security.jwt.JwtUser;
import com.decade.practice.infra.security.jwt.JwtUserAuthentication;
import com.decade.practice.infra.security.models.UserClaims;
import com.decade.practice.utils.TokenUtils;
import lombok.AllArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Order
public class AuthenticatorInterceptor implements ChannelInterceptor {
    private final UserClaimsService tokenService;

    @Nullable
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            String authHeader = accessor.getFirstNativeHeader("Authorization");
            String accessToken = TokenUtils.extractToken(authHeader);

            if (accessToken == null) {
                throw new AccessDeniedException("Invalid access token");
            }

            UserClaims userClaims = tokenService.decodeToken(accessToken);
            JwtUserAuthentication authentication = new JwtUserAuthentication(new JwtUser(userClaims), accessToken);
            accessor.setUser(authentication);
        }

        return message;

    }
}
