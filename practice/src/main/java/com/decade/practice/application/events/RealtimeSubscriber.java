package com.decade.practice.application.events;

import com.decade.practice.api.dto.EventDto;
import com.decade.practice.infra.configs.WebSocketConfiguration;
import com.decade.practice.persistence.redis.TypeEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

import static com.decade.practice.api.websocket.ConversationController.resolveChatDestination;

@Slf4j
@Component
@AllArgsConstructor
public class RealtimeSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final ChannelTopic queueTopic;
    private final ChannelTopic chatTopic;
    private final SimpMessagingTemplate template;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {

            String channel = new String(
                    message.getChannel(),
                    StandardCharsets.UTF_8
            );
            String json = new String(message.getBody(), StandardCharsets.UTF_8);
            if (channel.equals(queueTopic.getTopic())) {
                EventDto event = objectMapper.readValue(json, EventDto.class);
                template.convertAndSendToUser(
                        event.getOwner().getUsername(),
                        WebSocketConfiguration.QUEUE_DESTINATION,
                        event
                );
            } else if (channel.equals(chatTopic.getTopic())) {
                TypeEvent event = objectMapper.readValue(json, TypeEvent.class);
                template.convertAndSend(
                        resolveChatDestination(event.getChat()),
                        event
                );
            }
            log.debug("Received user event: {}", json);
        } catch (Exception e) {
            log.error("Failed to receive event", e);
        }
    }
}
