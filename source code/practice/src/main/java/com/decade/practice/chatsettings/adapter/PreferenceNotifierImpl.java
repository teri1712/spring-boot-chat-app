package com.decade.practice.chatsettings.adapter;

import com.decade.practice.chatsettings.domain.Preference;
import com.decade.practice.chatsettings.dto.PreferenceMapper;
import com.decade.practice.chatsettings.ports.out.PreferenceNotifier;
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
