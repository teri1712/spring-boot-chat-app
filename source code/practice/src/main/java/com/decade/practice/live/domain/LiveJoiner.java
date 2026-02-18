package com.decade.practice.live.domain;

import com.decade.practice.live.domain.events.JoinerLeaved;
import com.decade.practice.live.domain.events.JoinerTyped;
import com.decade.practice.live.domain.events.LiveJoined;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@RedisHash(value = "type")
public class LiveJoiner extends AbstractAggregateRoot<LiveJoiner> {

    private UUID userId;
    private LiveChatId chatId;

    @Nullable
    private Instant typeTime;

    @TimeToLive
    private Long joinDuration;

    @Id
    private String key;


    protected LiveJoiner() {
    }

    public LiveJoiner(LiveChatId chatId, UUID userId) {
        this.userId = userId;
        this.chatId = chatId;
        this.key = determineKey(userId, chatId);
        this.joinDuration = 0L;
    }

    public void join() {
        registerEvent(new LiveJoined(chatId, userId));
    }


    public void leave() {
        registerEvent(new JoinerLeaved(chatId, userId));
    }

    public void type() {
        this.typeTime = Instant.now();
        this.joinDuration = 2L;
        registerEvent(new JoinerTyped(chatId, userId, typeTime));
    }

    public static String determineKey(UUID from, LiveChatId chat) {
        return chat.value() + ":" + from;
    }

}