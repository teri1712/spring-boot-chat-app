package com.decade.practice.persistence.redis;

import com.decade.practice.persistence.jpa.embeddables.ImageSpec;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@RedisHash(value = "ONLINE", timeToLive = 5 * 60L)
public class OnlineStatus {

    @Id
    private String username;

    // TODO: Adjust client to new schema
    private UUID userId;
    private Instant at;
    private String name;
    private ImageSpec avatar;

}