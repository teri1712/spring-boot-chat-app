package com.decade.practice.live.application.services;

import com.decade.practice.live.application.ports.in.QueueService;
import com.decade.practice.live.application.ports.out.LiveBroker;
import com.decade.practice.live.domain.events.JoinerTyped;
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
public class RedisPubSubBroker implements LiveBroker, QueueService {

      @Value("${broker.topics.queue}")
      private String queueTopic;

      @Value("${broker.topics.live}")
      private String liveTopic;

      private final RedisMessageListenerContainer container;
      private final ConcurrentHashMap<String, Long> topicCountMap = new ConcurrentHashMap<>();
      private final RelayListener listener;
      private final RedisTemplate<String, Object> redisTemplate;

      @Override
      public void send(JoinerTyped typeEvent) {
            redisTemplate.convertAndSend(liveTopic + ":" + typeEvent.chatId(), new TypeMessage(typeEvent.userId(), typeEvent.avatar(), typeEvent.chatId(), typeEvent.at()));
      }

      @Override
      public void subLive(String liveChatId) {
            Topic topic = new PatternTopic(liveTopic + ":" + liveChatId);
            topicCountMap.compute(topic.getTopic(), (s, aLong) -> {
                  if (aLong == null) {
                        container.addMessageListener(listener, topic);
                        return 1L;
                  }
                  return aLong + 1;
            });
      }

      @Override
      public void unSubLive(String liveChatId) {
            Topic topic = new PatternTopic(liveTopic + ":" + liveChatId);
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
