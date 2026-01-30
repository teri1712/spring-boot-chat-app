package com.decade.practice.domain;

import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("accessPolicy")
public class MyChatOnlyAccessPolicy implements AccessPolicy {
    @Override
    public boolean isAllowed(ChatIdentifier chatIdentifier, UUID userId) {
        return chatIdentifier.getFirstUser().equals(userId) || chatIdentifier.getSecondUser().equals(userId);
    }
}
