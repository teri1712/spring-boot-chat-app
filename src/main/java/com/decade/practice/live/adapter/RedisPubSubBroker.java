package com.decade.practice.live.adapter;

import com.decade.practice.live.application.ports.out.LivenessBroker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisPubSubBroker implements LivenessBroker {

    private final RedisMessageListenerContainer container;
    private final ConcurrentHashMap<String, Long> topicCountMap = new ConcurrentHashMap<>();
    private final RelayListener listener;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void send(String des, Object message) {
        redisTemplate.convertAndSend(des, message);
    }

    @Override
    public void sub(String des) {
        Topic topic = new PatternTopic(des);
        topicCountMap.compute(topic.getTopic(), (s, aLong) -> {
            if (aLong == null) {
                container.addMessageListener(listener, topic);
                return 1L;
            }
            return aLong + 1;
        });
    }

    @Override
    public void unSub(String des) {
        Topic topic = new PatternTopic(des);
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
