package com.decade.practice.live.domain;

import com.decade.practice.live.domain.events.JoinerJoined;
import com.decade.practice.live.domain.events.JoinerLeaved;
import com.decade.practice.live.domain.events.JoinerTyped;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@RedisHash(value = "type")
public class LiveJoiner extends AbstractAggregateRoot<LiveJoiner> {

      private UUID userId;
      private String avatar;
      private String chatId;

      @Nullable
      private Instant typeTime;

      @TimeToLive
      private Long joinDuration;

      @Id
      private String key;


      protected LiveJoiner() {
      }

      public LiveJoiner(String chatId, UUID userId, String avatar) {
            this.userId = userId;
            this.chatId = chatId;
            this.avatar = avatar;
            this.key = determineKey(userId, chatId);
            this.joinDuration = 0L;
      }

      public void join() {
            registerEvent(new JoinerJoined(chatId, userId));
      }


      public void leave() {
            registerEvent(new JoinerLeaved(chatId, userId));
      }

      public void type() {
            this.joinDuration = 10L;
            Instant now = Instant.now();
            Instant prev = this.typeTime;

            if (prev != null) {
                  Duration typeDuration = Duration.between(prev, now);
                  if (typeDuration.toSeconds() <= 1) {
                        return;
                  }
            }
            this.typeTime = now;
            registerEvent(new JoinerTyped(chatId, userId, avatar, now));
      }

      public static String determineKey(UUID from, String chat) {
            return chat + ":" + from;
      }

}