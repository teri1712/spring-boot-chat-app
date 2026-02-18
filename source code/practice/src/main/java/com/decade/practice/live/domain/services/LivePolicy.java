package com.decade.practice.live.domain.services;

import com.decade.practice.engagement.api.EngagementFacade;
import com.decade.practice.engagement.api.EngagementRule;
import com.decade.practice.live.domain.LiveJoiner;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LivePolicy {

    private final EngagementFacade engagement;

    public void leave(LiveJoiner joiner) {
        EngagementRule participant = engagement.find(joiner.getChatId().value(), joiner.getUserId());
        if (!participant.write()) {
            throw new AccessDeniedException("You are not allowed to perform this operation");
        }
        joiner.leave();
    }

    public void join(LiveJoiner joiner) {
        EngagementRule participant = engagement.find(joiner.getChatId().value(), joiner.getUserId());
        if (!participant.read()) {
            throw new AccessDeniedException("You are not allowed to perform this operation");
        }
        joiner.join();
    }

    public void send(LiveJoiner joiner) {
        EngagementRule participant = engagement.find(joiner.getChatId().value(), joiner.getUserId());
        if (!participant.write()) {
            throw new AccessDeniedException("You are not allowed to perform this operation");
        }
        joiner.type();
    }
}
