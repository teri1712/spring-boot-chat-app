package com.decade.practice.live.infras;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    public static final String HANDSHAKE_DESTINATION = "/handshake";

    @Value("${frontend.host.address}")
    private String frontEndAddress;

    @Value("${websocket.topics.queue}")
    private String queueTopic;

    @Value("${websocket.topics.user}")
    private String userTopic;

    @Value("${websocket.topics.live}")
    private String liveTopic;


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

    @Bean
    public TaskScheduler heartBeatScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(queueTopic, liveTopic)
                .setTaskScheduler(heartBeatScheduler())
                .setHeartbeatValue(new long[]{5000L, 5000L});
        registry.setUserDestinationPrefix(userTopic);
    }

    @Bean
    @Order(1)
    public SecurityFilterChain handshakeFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(HANDSHAKE_DESTINATION)
                .requestCache(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize ->
                        authorize.anyRequest().permitAll()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
        return http.build();
    }
}