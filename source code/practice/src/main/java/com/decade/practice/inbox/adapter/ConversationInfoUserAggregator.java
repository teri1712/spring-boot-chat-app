package com.decade.practice.inbox.adapter;

import com.decade.practice.inbox.domain.ConversationInfo;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Stream;

@Component
public class ConversationInfoUserAggregator implements UserIdsAggregator<ConversationInfo> {
    @Override
    public Stream<UUID> aggregate(ConversationInfo sth) {
        return sth.getRepresentatives().stream();
    }
}
