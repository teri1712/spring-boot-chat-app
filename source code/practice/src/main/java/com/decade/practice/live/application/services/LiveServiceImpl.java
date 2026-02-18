package com.decade.practice.live.application.services;

import com.decade.practice.live.application.ports.in.LiveService;
import com.decade.practice.live.application.ports.out.JoinerRepository;
import com.decade.practice.live.domain.LiveChatId;
import com.decade.practice.live.domain.LiveJoiner;
import com.decade.practice.live.domain.services.LivePolicy;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class LiveServiceImpl implements LiveService {
    private final JoinerRepository joiners;
    private final LivePolicy engagementService;

    @Override
    public void join(LiveChatId liveChatId, UUID userId) {
        LiveJoiner joiner = new LiveJoiner(liveChatId, userId);
        engagementService.join(joiner);
        joiners.save(joiner);
    }

    @Override
    public void leave(LiveChatId liveChatId, UUID userId) {
        String key = LiveJoiner.determineKey(userId, liveChatId);
        LiveJoiner joiner = joiners.findById(key)
                .orElse(new LiveJoiner(liveChatId, userId));

        engagementService.leave(joiner);
        joiners.delete(joiner);
    }

    @Override
    public void send(LiveChatId liveChatId, UUID userId) {
        String key = LiveJoiner.determineKey(userId, liveChatId);
        LiveJoiner joiner = joiners.findById(key)
                .orElse(new LiveJoiner(liveChatId, userId));

        joiner.type();
        engagementService.send(joiner);
        joiners.save(joiner);
    }
}
