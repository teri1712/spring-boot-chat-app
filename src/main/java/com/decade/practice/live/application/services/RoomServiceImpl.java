package com.decade.practice.live.application.services;

import com.decade.practice.engagement.api.WritePolicy;
import com.decade.practice.live.application.ports.in.RoomService;
import com.decade.practice.live.application.ports.out.JoinerRepository;
import com.decade.practice.live.application.ports.out.LivenessBroker;
import com.decade.practice.live.domain.RoomJoiner;
import com.decade.practice.live.dto.TypeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RoomServiceImpl extends RoomService {
    final JoinerRepository joiners;

    @Value("${broker.topics.room}")
    String roomTopic;

    public RoomServiceImpl(LivenessBroker broker, JoinerRepository joiners) {
        super(broker);
        this.joiners = joiners;
    }

    @Override
    protected String getTopic() {
        return roomTopic;
    }

    @Override
    protected void onJoin(String chatId, UUID userId, String avatar) {
        RoomJoiner joiner = new RoomJoiner(chatId, userId, avatar);
        joiner.join();
        joiners.save(joiner);
    }

    @Override
    protected void onLeave(String chatId, UUID userId, String avatar) {
        String key = RoomJoiner.determineKey(userId, chatId);
        joiners.findById(key).ifPresent(joiner -> {
            joiner.leave();
            joiners.delete(joiner);
        });
    }

    @Override
    @WritePolicy
    public void type(String chatId, UUID userId, String avatar) {
        String key = RoomJoiner.determineKey(userId, chatId);
        joiners.findById(key).ifPresent(joiner -> {
            joiner.type();
            joiners.save(joiner);
            broker.send(toDestination(chatId), new TypeMessage(userId, avatar, chatId, joiner.getTypeTime()));
        });
    }
}
