package com.decade.practice.inbox.adapter;

import com.decade.practice.inbox.dto.InboxLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class LogUserAggregator implements UserIdsAggregator<InboxLogResponse> {
    private final ConversationInfoUserAggregator infoAggregator;
    private final MessageStateUserAggregator messageAggregator;

    @Override
    public Stream<UUID> aggregate(InboxLogResponse sth) {
        return
            Stream.concat(
                messageAggregator.aggregate(sth.messageState()),
                Stream.concat(infoAggregator.aggregate(sth.info()), Stream.of(sth.senderId()))
            );
    }
}
