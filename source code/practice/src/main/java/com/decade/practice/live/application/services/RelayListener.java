package com.decade.practice.live.application.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class RelayListener implements MessageListener {

    @Value("${broker.topics.queue}")
    private String brokerQueueTopic;

    @Value("${broker.topics.live}")
    private String brokerLiveTopic;

    @Value("${websocket.topics.user}")
    private String userTopic;

    @Value("${websocket.topics.queue}")
    private String queueTopic;

    @Value("${websocket.topics.live}")
    private String liveTopic;


    private final SimpMessagingTemplate template;


    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {

            String channel = new String(
                    message.getChannel(),
                    StandardCharsets.UTF_8
            );
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            if (channel.startsWith(brokerQueueTopic)) {
                String userId = channel.split(":")[1];
                template.convertAndSendToUser(
                        userId,
                        userTopic + queueTopic,
                        body
                );
            } else if (channel.startsWith(brokerLiveTopic)) {
                String chatId = channel.split(":")[1];

                template.convertAndSend(
                        liveTopic + "/" + chatId,
                        body
                );
            }
            log.debug("Received user event: {}", body);
        } catch (Exception e) {
            log.error("Failed to receive event", e);
        }
    }
}
