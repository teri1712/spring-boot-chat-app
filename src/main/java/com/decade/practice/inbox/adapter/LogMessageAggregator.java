package com.decade.practice.inbox.adapter;

import com.decade.practice.inbox.domain.messages.InboxLogMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class LogMessageAggregator implements UserIdsAggregator<InboxLogMessage> {
    private final ConversationInfoUserAggregator infoAggregator;
    private final MessageStateUserAggregator messageAggregator;

    @Override
    public Stream<UUID> aggregate(InboxLogMessage sth) {
        return
            Stream.concat(
                messageAggregator.aggregate(sth.messageState()),
                Stream.concat(infoAggregator.aggregate(sth.info()), Stream.of(sth.senderId(), sth.ownerId()))
            );
    }
}
