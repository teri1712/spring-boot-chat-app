package com.decade.practice.inbox.adapter;

import com.decade.practice.inbox.dto.ConversationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class ConversationUserAggregator implements UserIdsAggregator<ConversationResponse> {

    private final MessageStateUserAggregator messageAggregator;
    private final ConversationInfoUserAggregator infoAggregator;

    @Override
    public Stream<UUID> aggregate(ConversationResponse sth) {
        return Stream.concat(infoAggregator.aggregate(sth.info()),
            messageAggregator.aggregate(sth.recents())
        );
    }
}
