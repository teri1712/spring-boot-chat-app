package com.decade.practice.presence.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;
import java.util.UUID;

@Getter
@RedisHash(value = "user-presences", timeToLive = 300)
@AllArgsConstructor
@NoArgsConstructor
public class Presence {

      @Id
      private UUID userId;
      private Instant at;

}