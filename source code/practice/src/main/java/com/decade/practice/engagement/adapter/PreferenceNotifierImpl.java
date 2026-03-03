package com.decade.practice.engagement.adapter;

import com.decade.practice.engagement.application.ports.out.PreferenceNotifier;
import com.decade.practice.engagement.domain.Preference;
import com.decade.practice.engagement.dto.mapper.PreferenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PreferenceNotifierImpl implements PreferenceNotifier {

      private final RedisTemplate<String, Object> redisTemplate;
      private final PreferenceMapper preferenceMapper;

      @Value("${broker.topics.live}")
      private String liveTopic;

      @Override
      public void notify(String chatId, Preference preference) {
            redisTemplate.convertAndSend(liveTopic + ":" + chatId, preferenceMapper.map(preference));
      }
}
