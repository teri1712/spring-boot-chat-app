package com.decade.practice.presence.application.events;

import com.decade.practice.engagement.domain.events.ChatCreated;
import com.decade.practice.inbox.domain.events.ChatEventCreated;
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
      public void on(ChatEventCreated event) {
            UUID userId = event.getSenderId();
            String chatId = event.getChatId();


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
      public void on(ChatCreated event) {
            String chatId = event.chatId();

            event.participants().forEach(participant ->
                      redisTemplate.opsForZSet()
                                .addIfAbsent(determineEnthusiastId(chatId), participant, getCurrentTime()));
            resetActivity(chatId);
      }
}
