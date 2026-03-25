package com.decade.practice.live.adapter;

import com.decade.practice.live.application.ports.in.QueueService;
import com.decade.practice.live.application.ports.out.RoomBroker;
import com.decade.practice.live.dto.TypeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisPubSubBroker implements RoomBroker, QueueService {

      @Value("${broker.topics.queue}")
      private String queueTopic;

      @Value("${broker.topics.room}")
      private String roomTopic;

      private final RedisMessageListenerContainer container;
      private final ConcurrentHashMap<String, Long> topicCountMap = new ConcurrentHashMap<>();
      private final RelayListener listener;
      private final RedisTemplate<String, Object> redisTemplate;

      @Override
      public void send(TypeMessage message) {
            redisTemplate.convertAndSend(roomTopic + ":" + message.chatId(), message);
      }

      @Override
      public void subRoom(String chatId) {
            Topic topic = new PatternTopic(roomTopic + ":" + chatId);
            topicCountMap.compute(topic.getTopic(), (s, aLong) -> {
                  if (aLong == null) {
                        container.addMessageListener(listener, topic);
                        return 1L;
                  }
                  return aLong + 1;
            });
      }

      @Override
      public void unSubRoom(String chatId) {
            Topic topic = new PatternTopic(roomTopic + ":" + chatId);
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
            Topic topic = new PatternTopic(queueTopic + ":" + userId);
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
            Topic topic = new PatternTopic(queueTopic + ":" + userId);
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
