package com.decade.practice.live.application.events;

import com.decade.practice.live.application.ports.out.LivenessBroker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueManagement {


    @Value("${websocket.topics.user}")
    private String userTopic;

    @Value("${websocket.topics.queue}")
    private String queueTopic;

    @Value("${broker.topics.queue}")
    private String brokerQueueTopic;

    private final LivenessBroker broker;


    private String toBrokerDestination(UUID userId) {
        return brokerQueueTopic + ":" + userId;
    }

    @EventListener
    public void subQueue(SessionSubscribeEvent event) {
        if (isQueueDestination(event.getMessage())) {
            UUID userId = UUID.fromString(event.getUser().getName());
            broker.sub(toBrokerDestination(userId));
        }
    }

    private boolean isQueueDestination(Message<?> message) {
        StompHeaderAccessor accessor =
            MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        String destination = accessor.getDestination();
        return destination != null && destination.startsWith(userTopic + queueTopic);
    }

    @EventListener
    public void onUnsubscribe(SessionUnsubscribeEvent event) {
        if (isQueueDestination(event.getMessage())) {
            UUID userId = UUID.fromString(event.getUser().getName());
            broker.unSub(toBrokerDestination(userId));
        }
    }
}
