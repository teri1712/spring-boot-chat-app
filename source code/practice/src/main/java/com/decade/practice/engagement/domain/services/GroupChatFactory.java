package com.decade.practice.engagement.domain.services;

import org.springframework.stereotype.Service;

@Service
public class GroupChatFactory extends ChatFactory {
    public GroupChatFactory() {
        super(new GroupChatIdentifierMaker());
    }
}
