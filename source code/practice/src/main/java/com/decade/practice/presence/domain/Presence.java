package com.decade.practice.presence.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;
import java.util.UUID;

@Getter
@RedisHash(value = "PRESENCE", timeToLive = 5 * 60L)
@AllArgsConstructor
public class Presence {

    @Id
    private String username;

    // TODO: Adjust client to new schema
    private UUID userId;
    private Instant at;
    private String avatar;
    private String name;


    protected Presence() {
    }

}