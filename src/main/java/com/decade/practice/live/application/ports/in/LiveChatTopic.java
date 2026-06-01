package com.decade.practice.live.application.ports.in;

import com.decade.practice.engagement.api.ReadPolicy;
import com.decade.practice.engagement.api.WritePolicy;
import com.decade.practice.live.application.ports.out.LivenessBroker;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public abstract class LiveChatTopic {

    protected final LivenessBroker broker;

    protected abstract String getTopic();

    protected String toDestination(String chatId) {
        return getTopic() + ":" + chatId;
    }

    @ReadPolicy
    public void join(String chatId, UUID userId, String avatar) {
        broker.sub(getTopic() + ":" + chatId);
        onJoin(chatId, userId, avatar);
    }

    protected abstract void onJoin(String chatId, UUID userId, String avatar);

    protected abstract void onLeave(String chatId, UUID userId, String avatar);

    @WritePolicy
    public void leave(String chatId, UUID userId, String avatar) {
        broker.unSub(getTopic() + ":" + chatId);
        onLeave(chatId, userId, avatar);
    }

}
