package com.decade.practice.adapter.websocket.guard;

import com.decade.practice.infra.configs.WebSocketConfiguration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class BrokerGuard implements ChannelInterceptor {

        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
                String destination = SimpMessageHeaderAccessor.getDestination(message.getHeaders());
                if (destination == null) {
                        return message;
                }

                for (String prefix : WebSocketConfiguration.BROKER_DESTINATIONS) {
                        if (destination.startsWith(prefix)) {
                                return null; // Block the message
                        }
                }

                return message;
        }
}