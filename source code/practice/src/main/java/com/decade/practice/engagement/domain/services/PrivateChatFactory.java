package com.decade.practice.engagement.domain.services;

import com.decade.practice.engagement.domain.ChatCreators;
import org.springframework.stereotype.Service;

@Service
public class PrivateChatFactory extends ChatFactory {
    public PrivateChatFactory() {
        super(new TwoParticipantChatIdentifierMaker());
    }

    public String inspectIdentifier(ChatCreators chatCreators) {
        return chatIdentifierMaker.make(chatCreators);
    }

}
