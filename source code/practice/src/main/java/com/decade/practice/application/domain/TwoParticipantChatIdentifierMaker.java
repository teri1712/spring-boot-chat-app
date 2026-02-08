package com.decade.practice.application.domain;

import com.decade.practice.persistence.jpa.embeddables.ChatCreators;
import org.springframework.stereotype.Service;

@Service
public class TwoParticipantChatIdentifierMaker implements ChatIdentifierMaker {
    @Override
    public String make(ChatCreators creators) {
        return creators.firstCreator() + "+" + creators.secondCreator();
    }
}
