package com.decade.practice.engagement.domain.services;

import com.decade.practice.engagement.domain.ChatCreators;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GroupChatIdentifierMaker implements ChatIdentifierMaker {
    @Override
    public String make(ChatCreators creators) {
        return UUID.randomUUID().toString();
    }
}
