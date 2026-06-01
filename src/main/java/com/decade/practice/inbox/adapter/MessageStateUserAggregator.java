package com.decade.practice.inbox.adapter;

import com.decade.practice.inbox.dto.MessageStateResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Stream;

@Component
public class MessageStateUserAggregator implements UserIdsAggregator<
    MessageStateResponse> {
    @Override
    public Stream<UUID> aggregate(MessageStateResponse sth) {
        return Stream.concat(sth.getSeenByIds().stream(), Stream.of(sth.getSenderId()));
    }
}
