package com.decade.practice.live.application.services;

import com.decade.practice.engagement.api.ReadPolicy;
import com.decade.practice.engagement.api.WritePolicy;
import com.decade.practice.live.application.ports.in.RoomService;
import com.decade.practice.live.application.ports.out.JoinerRepository;
import com.decade.practice.live.domain.RoomJoiner;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final JoinerRepository joiners;

    @Override
    @ReadPolicy
    public void join(String chatId, UUID userId, String avatar) {
        RoomJoiner joiner = new RoomJoiner(chatId, userId, avatar);
        joiner.join();
        joiners.save(joiner);
    }

    @Override
    @WritePolicy
    public void leave(String chatId, UUID userId, String avatar) {
        String key = RoomJoiner.determineKey(userId, chatId);
        joiners.findById(key).ifPresent(joiner -> {
            joiner.leave();
            joiners.delete(joiner);
        });
    }

    @Override
    @WritePolicy
    public void send(String chatId, UUID userId, String avatar) {
        String key = RoomJoiner.determineKey(userId, chatId);
        joiners.findById(key).ifPresent(joiner -> {
            joiner.type();
            joiners.save(joiner);
        });
    }
}
