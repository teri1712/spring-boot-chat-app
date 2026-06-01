package com.decade.practice.live.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class RelayListener implements MessageListener {

    @Value("${broker.topics.queue}")
    private String brokerQueueTopic;

    @Value("${websocket.topics.queue}")
    private String queueTopic;

    private final SimpMessagingTemplate template;

    public RelayListener(@Lazy SimpMessagingTemplate template) {
        this.template = template;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String channel = new String(
                message.getChannel(),
                StandardCharsets.UTF_8
            );
            byte[] body = message.getBody();
            if (channel.startsWith(brokerQueueTopic)) {
                String userId = channel.split(":")[1];
                template.convertAndSendToUser(
                    userId,
                    queueTopic,
                    body,
                    m -> {
                        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(m);
                        accessor.setContentType(MimeTypeUtils.APPLICATION_JSON);
                        accessor.setNativeHeader("content-type", MimeTypeUtils.APPLICATION_JSON_VALUE);
                        return MessageBuilder.createMessage(m.getPayload(), accessor.getMessageHeaders());
                    }
                );
            } else {
                String websocketPath = channel.replace(':', '/');
                if (!websocketPath.startsWith("/"))
                    websocketPath = "/" + websocketPath;
                template.convertAndSend(
                    websocketPath,
                    body,
                    m -> {
                        SimpMessageHeaderAccessor accessor =
                            SimpMessageHeaderAccessor.wrap(m);
                        accessor.setContentType(MimeTypeUtils.APPLICATION_JSON);
                        accessor.setNativeHeader("content-type", MimeTypeUtils.APPLICATION_JSON_VALUE);
                        return MessageBuilder.createMessage(m.getPayload(), accessor.getMessageHeaders());
                    }
                );
            }
            log.debug("Received user currentState");
        } catch (Exception e) {
            log.error("Failed to receive currentState", e);
        }
    }
}
