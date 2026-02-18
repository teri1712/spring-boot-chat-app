package com.decade.practice.live.application.services;

import com.decade.practice.live.application.ports.in.QueueService;
import com.decade.practice.live.application.ports.out.LiveBroker;
import com.decade.practice.live.domain.LiveChatId;
import com.decade.practice.live.dto.TypeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class RedisPubSubBroker implements LiveBroker, QueueService {

    @Value("${broker.topics.queue}")
    private String queueTopic;

    @Value("${broker.topics.live}")
    private String liveTopic;

    private final RedisMessageListenerContainer container;
    private final ConcurrentHashMap<String, Long> topicCountMap = new ConcurrentHashMap<>();
    private final RelayListener listener;


    private final RedisTemplate<String, Object> redisTemplate;


    private String makeLiveTopic(LiveChatId liveChatId) {
        return liveTopic + ":" + liveChatId;
    }

    private String makeQueueTopic(UUID userId) {
        return queueTopic + ":" + userId;
    }

    @Override
    public void send(TypeMessage typeMessage) {
        redisTemplate.convertAndSend(makeLiveTopic(typeMessage.chatId()), typeMessage);
    }

    @Override
    public void subLive(LiveChatId liveChatId) {
        Topic topic = new PatternTopic(makeLiveTopic(liveChatId));
        topicCountMap.compute(topic.getTopic(), (s, aLong) -> {
            if (aLong == null) {
                container.addMessageListener(listener, topic);
                return 1L;
            }
            return aLong + 1;
        });
    }

    @Override
    public void unSubLive(LiveChatId liveChatId) {
        Topic topic = new PatternTopic(makeLiveTopic(liveChatId));
        topicCountMap.compute(topic.getTopic(), (s, aLong) -> {
            if (aLong == null)
                aLong = 0L;
            aLong -= 1L;
            if (aLong <= 0) {
                container.removeMessageListener(listener, topic);
                return null;
            }
            return aLong;
        });
    }

    @Override
    public void subQueue(UUID userId) {
        Topic topic = new PatternTopic(makeQueueTopic(userId));
        topicCountMap.compute(topic.getTopic(), (s, aLong) -> {
            if (aLong == null) {
                container.addMessageListener(listener, topic);
                return 1L;
            }
            return aLong + 1;
        });
    }

    @Override
    public void unSubQueue(UUID userId) {
        Topic topic = new PatternTopic(makeQueueTopic(userId));
        topicCountMap.compute(topic.getTopic(), (s, aLong) -> {
            if (aLong == null)
                aLong = 0L;
            aLong -= 1L;
            if (aLong <= 0) {
                container.removeMessageListener(listener, topic);
                return null;
            }
            return aLong;
        });
    }
}
