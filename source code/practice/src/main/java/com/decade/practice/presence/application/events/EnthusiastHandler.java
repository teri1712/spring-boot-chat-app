package com.decade.practice.presence.application.events;

import com.decade.practice.engagement.domain.events.ChatCreatedAccepted;
import com.decade.practice.engagement.domain.events.ChatEventAccepted;
import com.decade.practice.engagement.domain.events.ChatSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static com.decade.practice.presence.utils.EnthusiastUtils.determineEnthusiastId;

@Service
@RequiredArgsConstructor
public class EnthusiastHandler {

      private final RedisTemplate<String, Object> redisTemplate;

      @ApplicationModuleListener
      public void on(ChatEventAccepted event) {
            UUID userId = event.getSenderId();
            String chatId = event.getSnapshot().chatId();


            redisTemplate.opsForZSet().addIfAbsent(determineEnthusiastId(chatId), userId, getCurrentTime());
            redisTemplate.opsForZSet().incrementScore(determineEnthusiastId(chatId), userId, getBonusDuration());
            redisTemplate.opsForZSet().reverseRangeWithScores(determineEnthusiastId(chatId), 0, 10);
            resetActivity(chatId);
      }

      private void resetActivity(String chatId) {
            redisTemplate.expire(determineEnthusiastId(chatId), Duration.ofDays(30));
      }

      private static double getCurrentTime() {
            return (double) Instant.now().getEpochSecond();
      }

      private static double getBonusDuration() {
            return Duration.ofHours(1).toSeconds();
      }

      @ApplicationModuleListener
      public void on(ChatCreatedAccepted event) {
            ChatSnapshot snapshot = event.getSnapshot();
            String chatId = snapshot.chatId();
            UUID user = snapshot.creators().get(0);
            UUID partner = snapshot.creators().get(1);

            redisTemplate.opsForZSet().add(determineEnthusiastId(chatId), user, getCurrentTime());
            redisTemplate.opsForZSet().add(determineEnthusiastId(chatId), partner, getCurrentTime());
            resetActivity(chatId);
      }
}
