package com.decade.practice.infra.configs;

import com.decade.practice.api.websocket.arguments.ChatIdentifierArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    public static final String HANDSHAKE_DESTINATION = "/handshake";
    public static final String USER_DESTINATION = "/user";
    public static final String QUEUE_DESTINATION = "/queue";
    public static final String CHAT_DESTINATION = "/chat";
    public static final String TYPING_DESTINATION = "/typing";

    public static final String USER_QUEUE_DESTINATION = USER_DESTINATION + QUEUE_DESTINATION;
    public static final Set<String> BROKER_DESTINATIONS = new HashSet<>() {{
        add(CHAT_DESTINATION);
        add(QUEUE_DESTINATION);
    }};


    @Value("${frontend.host.address}")
    private String frontEndAddress;


    private final List<ChannelInterceptor> interceptors;
    private final List<HandshakeInterceptor> handShakeInterceptors;


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(HANDSHAKE_DESTINATION)
                .addInterceptors(handShakeInterceptors.toArray(new HandshakeInterceptor[0]))
                .setAllowedOrigins(frontEndAddress);
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
    }

    @Bean
    public TaskScheduler heartBeatScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(BROKER_DESTINATIONS.toArray(new String[0]))
                .setTaskScheduler(heartBeatScheduler())
                .setHeartbeatValue(new long[]{5000L, 5000L});
        registry.setUserDestinationPrefix(USER_DESTINATION);
    }
}