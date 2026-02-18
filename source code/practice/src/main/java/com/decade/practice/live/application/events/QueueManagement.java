package com.decade.practice.live.application.events;

import com.decade.practice.live.application.ports.in.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QueueManagement {


    @Value("${websocket.topics.user}")
    private String userTopic;

    @Value("${websocket.topics.queue}")
    private String queueTopic;

    private final QueueService queueService;

    @EventListener
    public void subQueue(SessionSubscribeEvent event) {
        if (isQueueDestination(event.getMessage()))
            queueService.subQueue(UUID.fromString(event.getUser().getName()));
    }

    private boolean isQueueDestination(Message<?> message) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        String destination = accessor.getDestination();
        return destination != null && destination.contains(queueTopic);
    }

    @EventListener
    public void onUnsubscribe(SessionUnsubscribeEvent event) {
        if (isQueueDestination(event.getMessage())) {
            queueService.unSubQueue(UUID.fromString(event.getUser().getName()));
        }
    }
}
