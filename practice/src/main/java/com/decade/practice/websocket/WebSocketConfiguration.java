package com.decade.practice.websocket;

import com.decade.practice.websocket.arguments.ChatArgumentResolver;
import com.decade.practice.websocket.arguments.ChatIdentifierArgumentResolver;
import com.decade.practice.websocket.arguments.UserArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

        public static final String HANDSHAKE_DESTINATION = "/handshake";
        public static final String USER_DESTINATION = "/user";
        public static final String QUEUE_MESSAGE_DESTINATION = "/queue/message";
        public static final String QUEUE_CHAT_DESTINATION = "/queue/chat";
        public static final String TYPING_DESTINATION = "/typing";

        public static final String USER_QUEUE_DESTINATION = USER_DESTINATION + QUEUE_MESSAGE_DESTINATION;
        public static final Set<String> BROKER_DESTINATIONS = new HashSet<>() {{
                add(QUEUE_CHAT_DESTINATION);
                add(QUEUE_MESSAGE_DESTINATION);
        }};

        private final CachedEntityConversationRepository entityRepo;
        private final List<ChannelInterceptor> interceptors;
        private final List<HandshakeInterceptor> handShakeInterceptors;

        public WebSocketConfiguration(
                CachedEntityConversationRepository entityRepo,
                List<ChannelInterceptor> interceptors,
                List<HandshakeInterceptor> handShakeInterceptors
        ) {
                this.entityRepo = entityRepo;
                this.interceptors = interceptors;
                this.handShakeInterceptors = handShakeInterceptors;
        }

        @Override
        public void registerStompEndpoints(StompEndpointRegistry registry) {
                registry.addEndpoint(HANDSHAKE_DESTINATION)
                        .addInterceptors(new HandshakeInterceptor() {
                                @Override
                                public boolean beforeHandshake(
                                        ServerHttpRequest request,
                                        ServerHttpResponse response,
                                        WebSocketHandler wsHandler,
                                        Map<String, Object> attributes
                                ) throws Exception {
                                        return request.getPrincipal() != null;
                                }

                                @Override
                                public void afterHandshake(
                                        ServerHttpRequest request,
                                        ServerHttpResponse response,
                                        WebSocketHandler wsHandler,
                                        Exception exception
                                ) {
                                        // Empty implementation
                                }
                        })
                        .addInterceptors(handShakeInterceptors.toArray(new HandshakeInterceptor[0]))
                        .setAllowedOrigins("http://localhost:4200");
                // .withSockJS();
        }

        @Override
        public void configureClientInboundChannel(ChannelRegistration registration) {
                for (ChannelInterceptor interceptor : interceptors) {
                        registration.interceptors(interceptor);
                }
        }

        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
                argumentResolvers.add(new ChatIdentifierArgumentResolver());
                argumentResolvers.add(new UserArgumentResolver(entityRepo));
                argumentResolvers.add(new ChatArgumentResolver(entityRepo));
        }

        @Override
        public void configureMessageBroker(MessageBrokerRegistry registry) {
                registry.enableSimpleBroker(BROKER_DESTINATIONS.toArray(new String[0]));
                registry.setUserDestinationPrefix(USER_DESTINATION);
        }
}