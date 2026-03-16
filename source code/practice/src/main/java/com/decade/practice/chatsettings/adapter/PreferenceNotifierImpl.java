package com.decade.practice.chatsettings.adapter;

import com.decade.practice.chatsettings.application.ports.out.PreferenceNotifier;
import com.decade.practice.chatsettings.domain.messages.PreferenceMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PreferenceNotifierImpl implements PreferenceNotifier {

      private final RedisTemplate<String, Object> redisTemplate;

      @Value("${broker.topics.room}")
      private String roomTopic;

      @Override
      public void notify(String chatId, PreferenceMessage message) {
            redisTemplate.convertAndSend(roomTopic + ":" + chatId, message);
      }
}
