package com.decade.practice.engagement.domain.services;

import com.decade.practice.engagement.domain.ChatCreators;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TwoParticipantChatIdentifierMaker implements ChatIdentifierMaker {
    @Override
    public String make(ChatCreators creators) {
        UUID smaller, bigger;
        if (creators.firstCreator().compareTo(creators.secondCreator()) > 0) {
            smaller = creators.secondCreator();
            bigger = creators.firstCreator();
        } else {
            smaller = creators.firstCreator();
            bigger = creators.secondCreator();
        }
        return smaller + "+" + bigger;
    }
}
