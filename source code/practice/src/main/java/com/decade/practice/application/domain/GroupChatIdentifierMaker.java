package com.decade.practice.application.domain;

import com.decade.practice.persistence.jpa.embeddables.ChatCreators;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GroupChatIdentifierMaker implements ChatIdentifierMaker {
    @Override
    public String make(ChatCreators creators) {
        return UUID.randomUUID().toString();
    }
}
