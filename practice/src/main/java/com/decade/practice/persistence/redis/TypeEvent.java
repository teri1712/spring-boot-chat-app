package com.decade.practice.persistence.redis;

import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.utils.ChatUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@RedisHash(value = "type", timeToLive = 2)
public class TypeEvent {

    private UUID from;
    private ChatIdentifier chat;
    // TODO: Adjust client
    private Instant time = Instant.now();

    @Id
    private String key;

    /**
     * Determines a key based on the from UUID and chat identifier.
     * This is a utility method that was a top-level function in the Kotlin version.
     */
    public static String determineKey(UUID from, ChatIdentifier chat) {
        UUID partner = ChatUtils.inspectPartner(chat, from);
        return from + "->" + partner;
    }

}